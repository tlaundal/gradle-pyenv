package io.totokaka.gradle.pyenv.tasks

import io.totokaka.gradle.pyenv.Utils
import org.gradle.api.provider.Property
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory

@CacheableTask
class BuildPython extends AbstractExecTask {

    @InputDirectory
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

        executable("${ -> pythonBuildDirProp.get().getAbsolutePath()}/bin/python-build")
        args(["${ -> pythonProp.get()}", "${ -> targetProp.get().getAbsolutePath()}"])
    }

    static {
        Utils.dslify(BuildPython, 'pythonBuildDirProp', 'pythonBuildDir')
        Utils.dslify(BuildPython, 'pythonProp', 'python')
        Utils.dslify(BuildPython, 'targetProp', 'target')
    }

}
