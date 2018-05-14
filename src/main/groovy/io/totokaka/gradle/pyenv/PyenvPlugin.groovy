package io.totokaka.gradle.pyenv

import io.totokaka.gradle.pyenv.plugins.base.SimplePythonBasePlugin
import io.totokaka.gradle.pyenv.tasks.BuildPython
import io.totokaka.gradle.pyenv.tasks.CreateVenv
import io.totokaka.gradle.pyenv.tasks.ExtractPythonBuild
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * A gradle plugin that wraps pyenv, for easy use of python in gradle projects
 */
class PyenvPlugin implements Plugin<Project> {

    private static final String TASK_GROUP = 'Python'

    private Project project
    private PyenvExtension extension

    @Override
    void apply(Project project) {
        this.project = project
        this.extension = PyenvExtension.create(project)

        project.getPlugins().apply(SimplePythonBasePlugin)

        createDefaultTasks()
    }

    void createDefaultTasks() {
        project.tasks.create('extractPythonBuild', ExtractPythonBuild, this.&configureExtractPythonBuildTask)
        project.tasks.create('buildPython', BuildPython, this.&configureDefaultBuildPythonTask)
        project.tasks.create('createVenv', CreateVenv, this.&configureDefaultCreateVenvTask)
    }

    void configureExtractPythonBuildTask(ExtractPythonBuild task) {
        task.setGroup(TASK_GROUP)
        task.setDescription('Resolves pyenv through gradle dependency, and extracts python-build')

        task.target.set(extension.pythonBuildDirectoryProp)
    }

    void configureDefaultBuildPythonTask(BuildPython task) {
        task.setGroup(TASK_GROUP)
        task.setDescription('Builds python using python-build. Typically takes over 10 minutes')
        task.dependsOn(project.tasks['extractPythonBuild'])

        task.pythonBuildDir.set(extension.pythonBuildDirectoryProp)
        task.python.set(extension.pythonVersionProp)
        task.target.set(extension.prefixDirectoryProp)
    }

    void configureDefaultCreateVenvTask(CreateVenv task) {
        task.setGroup(TASK_GROUP)
        task.setDescription('Create a virtual python environment with venv')
        task.dependsOn(project.tasks['buildPython'])

        task.prefixProp.set(extension.prefixDirectoryProp)
        task.targetProp.set(extension.environmentProp)
    }

}
