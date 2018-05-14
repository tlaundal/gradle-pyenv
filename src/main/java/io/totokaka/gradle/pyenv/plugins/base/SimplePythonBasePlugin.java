package io.totokaka.gradle.pyenv.plugins.base;

import io.totokaka.gradle.pyenv.tasks.BuildPython;
import io.totokaka.gradle.pyenv.tasks.CreateVenv;
import io.totokaka.gradle.pyenv.tasks.VenvExec;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.repositories.IvyPatternRepositoryLayout;

/**
 * The base plugin for simple-python.
 *
 * Defines pyenv as a dependency for the project, and makes the tasks available
 */
public class SimplePythonBasePlugin implements Plugin<Project> {

    private final static String GITHUB_URL = "https://github.com/";
    private final static String GITHUB_PATTERN = "/[organisation]/[module]/archive/[revision].[ext]";
    private final static String PYENV_DEPENDENCY = "pyenv:pyenv:v1.2.4@zip";

    private Project project;

    @Override
    public void apply(Project target) {
        this.project = target;

        configurePyenvDependency();
        extendWithTaskTypes();
    }

    /**
     * Configures pyenv as an ivy dependency.
     *
     * Creates a dependency configuration named pyenv,
     * adds a repository for github releases and
     * adds pyenv as a default dependency in the pyenv configuration.
     */
    private void configurePyenvDependency() {
        project.getRepositories().ivy(repo -> {
            repo.setUrl(GITHUB_URL);
            repo.artifactPattern(GITHUB_PATTERN);
        });

        Configuration configuration = project.getConfigurations().create("pyenv")
            .setVisible(false)
            .setDescription("The pyenv dependency for the plugin");

        Dependency dependency = project.getDependencies().create(PYENV_DEPENDENCY);
        configuration.defaultDependencies(defaults -> defaults.add(dependency));
    }

    /**
     * Extends the project with access to the task types (without importing)
     */
    private void extendWithTaskTypes() {
        extendWithTaskType(BuildPython.class);
        extendWithTaskType(CreateVenv.class);
        extendWithTaskType(VenvExec.class);
    }

    /**
     * Helper method to extend the project with shorthand access to a type/class.
     *
     * @param type The type to add to the project's extra properties
     */
    private void extendWithTaskType(Class type) {
        project.getExtensions().getExtraProperties().set(type.getSimpleName(), type);
    }

}
