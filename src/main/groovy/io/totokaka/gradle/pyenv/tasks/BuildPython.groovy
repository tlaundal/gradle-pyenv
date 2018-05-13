package io.totokaka.gradle.pyenv.tasks

import io.totokaka.gradle.pyenv.DirectoryChecksumUtil
import io.totokaka.gradle.pyenv.Utils
import org.gradle.api.provider.Property
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

@CacheableTask
class BuildPython extends AbstractExecTask {

    @Internal
    Property<File> pythonBuildDirProp

    @Input
    Property<String> pythonProp

    @OutputDirectory
    Property<File> targetProp

    BuildPython() {
        super(BuildPython)
        this.pythonBuildDirProp = project.objects.property(File)
        this.pythonProp = project.objects.property(String)
        this.targetProp = project.objects.property(File)

        onlyIf(this.&notBuilt)
        executable("${ -> pythonBuildDirProp.get().getAbsolutePath()}/bin/python-build")
        args(["${ -> pythonProp.get()}", "${ -> targetProp.get().getAbsolutePath()}"])
        doLast(this.&writeChecksum)
    }

    static {
        Utils.dslify(BuildPython, 'pythonBuildDirProp', 'pythonBuildDir')
        Utils.dslify(BuildPython, 'pythonProp', 'python')
        Utils.dslify(BuildPython, 'targetProp', 'target')
    }

    boolean notBuilt() {
        File dir = targetProp.get()
        byte[] checksum
        try {
            checksum = getChecksumFile(dir).getBytes()
        } catch (Exception ignored) {
            checksum = []
        }

        return !DirectoryChecksumUtil.verifyDirectoryChecksum(dir, checksum)
    }

    void writeChecksum() {
        File dir = targetProp.get()
        byte[] checksum = DirectoryChecksumUtil.checksumDirectory(dir)
        getChecksumFile(dir) << checksum
    }

    static File getChecksumFile(File dir) {
        return new File(dir.parent, dir.name + ".checksum")
    }

}
