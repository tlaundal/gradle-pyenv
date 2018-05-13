package io.totokaka.gradle.pyenv.tasks

import io.totokaka.gradle.pyenv.PyenvExtension
import io.totokaka.gradle.pyenv.Utils
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec

/**
 * Executes a command inside the venv
 */
class VenvExec extends DefaultTask {

    @InputDirectory
    Property<File> venvProp

    @Input
    Property<Object> executableProp

    @Internal
    List<Object> arguments

    @Input
    Property<File> workingDirectoryProp

    @Internal
    ExecResult execResult

    VenvExec() {
        super()
        venvProp = project.objects.property(File)
        executableProp = project.objects.property(Object)
        workingDirectoryProp = project.objects.property(File)

        venvProp.set(project.extensions.getByType(PyenvExtension).environmentProp)
        workingDirectoryProp.set(project.projectDir)
    }

    static {
        Utils.dslify(VenvExec, 'venvProp', 'venv')
        Utils.dslify(VenvExec, 'executableProp', 'executable')
        Utils.dslify(VenvExec, 'workingDirectoryProp', 'workingDirectory')
    }

    void configureAction(ExecSpec spec) {
        String venv = this.venvProp.get().getAbsolutePath()

        spec.workingDir(workingDirectoryProp.get())

        // Simulate the venv activate script
        spec.environment('PATH', "$venv/bin:${spec.environment.get('PATH')}")
        spec.environment('VIRTUAL_ENV', venv)
        spec.environment.remove('PYTHONHOME')
        spec.setExecutable('bash')

        spec.setArgs(['-c', String.join(' ', [this.executableProp.get(), *this.arguments])])
    }

    @TaskAction
    void exec() {
        execResult = project.exec(this.&configureAction)
    }



    void arguments(Object... args) {
        this.setArguments(args.toList())
    }

    void arguments(List<Object> args) {
        this.setArguments(args)
    }

    @Input
    Serializable getArgumentsAsInput() {
        return String.join(' ', arguments.collect({it?.toString()}))
    }

}
