package io.totokaka.gradle.pyenv.plugins.simplePython;

import io.totokaka.gradle.pyenv.SupplierProvider;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;

import java.io.File;

public class SimplePythonExtension {

    private final Property<Object> bootstrapDirectory;
    private final Property<Object> buildDirectory;
    private final Property<Object> pythonHome;

    private final Property<Object> python;
    private final Property<Object> environment;

    public SimplePythonExtension(Project project) {
        this.bootstrapDirectory = project.getObjects().property(Object.class);
        this.buildDirectory = project.getObjects().property(Object.class);
        this.pythonHome = project.getObjects().property(Object.class);
        this.python = project.getObjects().property(Object.class);
        this.environment = project.getObjects().property(Object.class);

        this.bootstrapDirectory.set(SupplierProvider.of(() ->
            new File(project.getRootDir(), ".gradle/simple-python")));
        this.buildDirectory.set(bootstrapDirectory.map(
                bootstrap -> new File(project.file(bootstrap), "build")));
        this.pythonHome.set(bootstrapDirectory.map(
                bootstrap -> new File(project.file(bootstrap), "python")));
        this.python.set("3.6.0");
        this.environment.set(SupplierProvider.of(() ->
            new File(project.getBuildDir(), "venv")));
    }

    static SimplePythonExtension create(Project project) {
        return project.getExtensions()
                .create("simple-python", SimplePythonExtension.class, project);
    }

    /**
     * The parent directory for the build and python home directories.
     *
     * Defaults to "${project.rootDir}/.gradle/simple-python/"
     *
     * @return The directory used for downloads and compilations that should
     *         never or rarely be cleaned.
     */
    public Property<Object> getBootstrapDirectory() {
        return bootstrapDirectory;
    }

    /**
     * The directory to download pyenv's build-python into.
     *
     * Defaults to "${bootstrapDirectory}/build/"
     *
     * @return The directory for build-python.
     */
    public Property<Object> getBuildDirectory() {
        return buildDirectory;
    }

    /**
     * The directory python is built into, and the directory used as $PYTHONHOME
     * when creating a virtualenv.
     *
     * Defaults to "${bootstrapDirectory}/python/"
     *
     * @return The directory python is built into.
     */
    public Property<Object> getPythonHome() {
        return pythonHome;
    }

    /**
     * The python version to build and use for the virtual environment.
     *
     * Defaults to "3.6.0"
     *
     * @return The python version
     */
    public Property<Object> getPython() {
        return python;
    }

    /**
     * The directory to find the virtual environment in.
     *
     * This is used when creating a virtual environment, and when executing
     * VenvExec tasks
     *
     * @return The directory of the virtual environment
     */
    public Property<Object> getEnvironment() {
        return environment;
    }

}
