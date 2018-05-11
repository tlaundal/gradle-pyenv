package io.totokaka.gradle.pyenv.tasks

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory

class BuildPython extends Exec {

    @InputDirectory
    Property<File> pythonBuildDir

    @Input
    Property<String> python

    @OutputDirectory
    Property<File> target

    BuildPython() {
        super()
        this.pythonBuildDir = project.objects.property(File)
        this.python = project.objects.property(String)
        this.target = project.objects.property(File)

        executable("${ -> pythonBuildDir.get().getAbsolutePath()}/bin/python-build")
        args(["${ -> python.get()}", "${ -> target.get().getAbsolutePath()}"])
    }

}
