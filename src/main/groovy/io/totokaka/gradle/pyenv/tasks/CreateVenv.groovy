package io.totokaka.gradle.pyenv.tasks

import io.totokaka.gradle.pyenv.Utils
import org.gradle.api.provider.Property
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory

/**
 * Gradle task for creating a Python venv virtual environment.
 *
 * This task depends on Python3, as the venv python modules is used.
 */
class CreateVenv extends AbstractExecTask {

    /**
     * The directory python is found in.
     */
    @InputDirectory
    Property<File> prefixProp

    /**
     * The directory the venv should be placed in.
     */
    @OutputDirectory
    Property<File> targetProp

    CreateVenv() {
        super(CreateVenv)
        prefixProp = project.objects.property(File)
        targetProp = project.objects.property(File)

        environment('PYTHONHOME', "${ -> prefixProp.get().getAbsolutePath()}")
        executable("${ -> prefixProp.get().getAbsolutePath()}/bin/python")
        args(['-m', 'venv', "${ -> targetProp.get().getAbsolutePath()}"])
    }

    static {
        Utils.dslify(CreateVenv, 'prefixProp', 'prefix')
        Utils.dslify(CreateVenv, 'targetProp', 'target')
    }

}
