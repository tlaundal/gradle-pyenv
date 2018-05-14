package io.totokaka.gradle.pyenv.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Arrays;

/**
 * Gradle task for creating a Python virtual environment.
 *
 * This task needs a Python 3 distribution, as the venv Python module is used
 * to create the virtual environment.
 */
public class CreateVenv extends DefaultTask {

    private final Property<File> pythonHome;
    private final Property<File> target;

    public CreateVenv() {
        this.pythonHome = getProject().getObjects().property(File.class);
        this.target = getProject().getObjects().property(File.class);
    }

    @TaskAction
    void createVenv() {
        String pythonHome = this.pythonHome.get().getAbsolutePath();
        String target = this.pythonHome.get().getAbsolutePath();
        getProject().exec(spec -> {
            spec.getEnvironment().put("PYTHONHOME", pythonHome);
            spec.setExecutable("$prefix/bin/python");
            spec.setArgs(Arrays.asList("-m", "venv", target));
        });
    }

    /**
     * The directory containing the python distribution.
     *
     * This must point to a Python 3 distribution, with the venv module.
     *
     * @return The {@link Property} for the python home
     */
    @InputDirectory
    public Property<File> getPythonHome() {
        return this.pythonHome;
    }

    /**
     * The directory the virtual environment should be placed in.
     *
     * @return The {@link Property} for the virtual environment target.
     */
    @OutputDirectory
    public Property<File> getTarget() {
        return this.target;
    }

}
