package io.totokaka.gradle.pyenv.tasks

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory

class CreateVenv extends Exec {

    @InputDirectory
    Property<File> prefix

    @OutputDirectory
    Property<File> target

    CreateVenv() {
        super()
        prefix = project.objects.property(File)
        target = project.objects.property(File)

        environment('PYTHONHOME', "${ -> prefix.get().getAbsolutePath()}")
        executable("${ -> prefix.get().getAbsolutePath()}/bin/python")
        args(['-m', 'venv', "${ -> target.get().getAbsolutePath()}"])
    }

}
