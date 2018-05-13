package io.totokaka.gradle.pyenv.tasks

import io.totokaka.gradle.pyenv.Utils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task for creating a Python venv virtual environment.
 *
 * This task depends on Python3, as the venv python modules is used.
 */
class CreateVenv extends DefaultTask {

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
        prefixProp = project.objects.property(File)
        targetProp = project.objects.property(File)
    }

    static {
        Utils.dslify(CreateVenv, 'prefixProp', 'prefix')
        Utils.dslify(CreateVenv, 'targetProp', 'target')
    }

    @TaskAction
    void exec() {
        String prefix = prefixProp.get().getAbsolutePath()
        String target = targetProp.get().getAbsolutePath()
        project.exec({ spec ->
            spec.getEnvironment().put('PYTHONHOME', prefix)
            spec.setExecutable("$prefix/bin/python")
            spec.setArgs(['-m', 'venv', target])
        } as Action)
    }


}
