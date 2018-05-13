package io.totokaka.gradle.pyenv.tasks

import io.totokaka.gradle.pyenv.DirectoryChecksumUtil
import io.totokaka.gradle.pyenv.Utils
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task that builds Python from source using pyenv's build-python
 */
@CacheableTask
class BuildPython extends DefaultTask {

    /**
     * The directory python-build is found in.
     */
    @Internal
    Property<File> pythonBuildDirProp

    /**
     * The python version that should be built.
     *
     * For example: "3.6.0"
     */
    @Input
    Property<String> pythonProp

    /**
     * The target folder for the build.
     *
     * This is what later may be used as the python prefix.
     */
    @OutputDirectory
    Property<File> targetProp

    BuildPython() {
        this.pythonBuildDirProp = project.objects.property(File)
        this.pythonProp = project.objects.property(String)
        this.targetProp = project.objects.property(File)
    }

    static {
        Utils.dslify(BuildPython, 'pythonBuildDirProp', 'pythonBuildDir')
        Utils.dslify(BuildPython, 'pythonProp', 'python')
        Utils.dslify(BuildPython, 'targetProp', 'target')
    }

    void buildPython() {
        project.exec({ execAction ->
            execAction.executable("${pythonBuildDirProp.get().getAbsolutePath()}/bin/python-build")
            execAction.args([pythonProp.get(), targetProp.get().getAbsolutePath()])
        })
    }

    @TaskAction
    void exec() {
        if (this.alreadyBuilt()) {
            throw new StopExecutionException('Python already exists in this location, no need for building')
        }

        buildPython()

        writeChecksum()
    }

    boolean alreadyBuilt() {
        byte[] checksum
        try {
            checksum = getChecksumFile().getBytes()
        } catch (Exception ignored) {
            checksum = []
        }

        File target = targetProp.get()
        return target.isDirectory() && DirectoryChecksumUtil.verifyDirectoryChecksum(target, checksum)
    }

    void writeChecksum() {
        byte[] checksum = DirectoryChecksumUtil.checksumDirectory(targetProp.get())
        getChecksumFile() << checksum
    }

    @OutputFile
    File getChecksumFile() {
        File dir = targetProp.get()
        return new File(dir.parent, dir.name + ".checksum")
    }

}
