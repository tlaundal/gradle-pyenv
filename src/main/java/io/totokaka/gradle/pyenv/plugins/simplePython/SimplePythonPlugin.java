package io.totokaka.gradle.pyenv.plugins.simplePython;

import io.totokaka.gradle.pyenv.plugins.base.SimplePythonBasePlugin;
import io.totokaka.gradle.pyenv.tasks.BuildPython;
import io.totokaka.gradle.pyenv.tasks.CreateVenv;
import io.totokaka.gradle.pyenv.tasks.ExtractPythonBuild;
import io.totokaka.gradle.pyenv.tasks.VenvExec;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SimplePythonPlugin implements Plugin<Project> {

    private static final String TASK_GROUP = "Python";

    private Project project;
    private SimplePythonExtension extension;

    @Override
    public void apply(Project target) {
        this.project = target;
        this.extension = SimplePythonExtension.create(project);

        project.getPlugins().apply(SimplePythonBasePlugin.class);

        createDefaultTasks();
        project.afterEvaluate(this::configureVenvExecTasks);
    }

    private void configureVenvExecTasks(Project project) {
        project.getTasks().withType(VenvExec.class).forEach(
                task -> task.getVirtualEnvironment().set(extension.getEnvironment()));
    }

    private void createDefaultTasks() {
        project.getTasks().create(
                "extractPythonBuild",
                ExtractPythonBuild.class,
                this::configureExtractPythonBuildTask);
        project.getTasks().create(
                "buildPython",
                BuildPython.class,
                this::configureDefaultBuildPythonTask);
        project.getTasks().create(
                "createVenv",
                CreateVenv.class,
                this::configureDefaultCreateVenvTask);
    }

    private void configureExtractPythonBuildTask(ExtractPythonBuild task) {
        task.setGroup(TASK_GROUP);
        task.setDescription("Resolves pyenv through gradle dependency, and extracts python-build");

        task.getTarget().set(extension.getBuildDirectory());
    }

    private void configureDefaultBuildPythonTask(BuildPython task) {
        task.setGroup(TASK_GROUP);
        task.setDescription("Builds python using python-build. Typically takes over 10 minutes");
        task.dependsOn(project.getTasks().findByName("extractPythonBuild"));

        task.getPythonBuildDir().set(extension.getBuildDirectory());
        task.getPython().set(extension.getPython());
        task.getTarget().set(extension.getPythonHome());
    }

    private void configureDefaultCreateVenvTask(CreateVenv task) {
        task.setGroup(TASK_GROUP);
        task.setDescription("Create a virtual python environment with venv");
        task.dependsOn(project.getTasks().findByName("buildPython"));

        task.getPythonHome().set(extension.getPythonHome());
        task.getTarget().set(extension.getEnvironment());
    }

}
