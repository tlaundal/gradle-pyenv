package io.totokaka.gradle.pyenv.tasks

import io.totokaka.gradle.pyenv.PyenvExtension
import io.totokaka.gradle.pyenv.Utils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec

/**
 * Gradle task for executing commands inside a venv
 */
class VenvExec extends DefaultTask {

    /**
     * The directory of the venv
     */
    @InputDirectory
    Property<File> venvProp

    /**
     * Name of the executable, such as "python" or "pip", or a path to an executable
     */
    @Input
    Property<Object> executableProp

    /**
     * Arguments to pass to the executable
     */
    @Internal
    List<Object> arguments

    /**
     * The working directory for the process.
     * Defaults to {@link Project#getProjectDir}
     */
    @Input
    Property<File> workingDirectoryProp

    /**
     * The result of the process execution
     */
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

    /**
     * Append arguments to the executable
     *
     * @param args The arguments to append
     */
    void arguments(Object... args) {
        this.arguments.addAll(args)
    }

    /**
     * Append arguments to the executable
     *
     * @param args The arguments to append
     */
    void arguments(List<Object> args) {
        this.arguments.addAll(args)
    }

    /**
     * Gets the arguments in a Serializable way, for incremental build support.
     *
     * Only for use by Gradle.
     *
     * @return The arguments for the executable joined as a string, casted down to a Serializable
     */
    @Input
    Serializable getArgumentsAsInput() {
        return String.join(' ', arguments.collect({it?.toString()}))
    }

}
