package io.totokaka.gradle.pyenv.tasks;

import io.totokaka.gradle.pyenv.SupplierProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VenvExec extends DefaultTask {

    private final Property<File> virtualEnvironment;
    private final Property<Object> executable;
    private final Property<File> workingDirectory;
    private final ArrayList<Object> arguments;

    private ExecResult result;

    public VenvExec() {
        this.virtualEnvironment = getProject().getObjects().property(File.class);
        this.executable = getProject().getObjects().property(Object.class);
        this.workingDirectory = getProject().getObjects().property(File.class);
        this.arguments = new ArrayList<>();

        this.workingDirectory.set(SupplierProvider.of(getProject()::getProjectDir));
    }

    private void configureAction(ExecSpec spec) {
        String env = this.virtualEnvironment.get().getAbsolutePath();

        // Simulate the venv activate script
        Map<String, Object> environment = spec.getEnvironment();
        environment.remove("PYTHONHOME");
        environment.put("PATH", env + "/bin:" + environment.get("PATH"));
        environment.put("VIRTUAL_ENV", env);

        spec.setExecutable("bash");
        spec.setWorkingDir(workingDirectory.get());

        spec.setArgs(Arrays.asList("-c", Stream.concat(
                Stream.of(executable.get()), arguments.stream())
                        .map(Object::toString)
                        .collect(Collectors.joining(" "))));
    }

    @TaskAction
    void exec() {
        this.result = getProject().exec(this::configureAction);
    }

    /**
     * The directory of the virtual environment.
     *
     * @return The {@link Property} for the virtual environment
     */
    @Input
    public Property<File> getVirtualEnvironment() {
        return this.virtualEnvironment;
    }

    /**
     * The name of the executable.
     *
     * For example "python" or "pip"
     *
     * @return The {@link Property} for the executable
     */
    @Input
    public Property<Object> getExecutable() {
        return this.executable;
    }

    /**
     * The directory to use as the working directory for the process.
     *
     * @return The {@link Property} for the working directory.
     */
    @Input
    public Property<File> getWorkingDirectory() {
        return this.workingDirectory;
    }

    /**
     * The arguments to pass to the executable.
     *
     * @return The arguments.
     */
    @Input
    public ArrayList<Object> getArguments() {
        return this.arguments;
    }

    /**
     * The name of the executable.
     *
     * @return The executable
     */
    @Internal
    public Object getExec() {
        return this.getExecutable().get();
    }

    /**
     * Set the executable.
     *
     * @param exec The name of the executable
     */
    public void setExec(Object exec) {
        this.getExecutable().set(exec);
    }

    /**
     * The arguments to pass to the executable.
     *
     * @see VenvExec#getArguments()
     * @return The arguments.
     */
    @Internal
    public List<Object> getArgs() {
        return this.arguments;
    }

    /**
     * Set the arguments to pass to the executable.
     *
     * @param args The arguments.
     */
    public void setArgs(Collection<Object> args) {
        this.arguments.clear();
        this.arguments.addAll(args);
    }

    /**
     * Append arguments to the executable.
     *
     * @param args The arguments.
     */
    public void args(Object... args) {
        Collections.addAll(this.arguments, args);
    }

    /**
     * The result of the execution.
     *
     * @return The result of the execution.
     */
    @Internal
    public ExecResult getResult() {
        return this.result;
    }

}
