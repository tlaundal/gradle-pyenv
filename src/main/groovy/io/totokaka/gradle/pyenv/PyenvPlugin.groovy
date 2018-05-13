package io.totokaka.gradle.pyenv

import io.totokaka.gradle.pyenv.tasks.BuildPython
import io.totokaka.gradle.pyenv.tasks.CreateVenv
import io.totokaka.gradle.pyenv.tasks.VenvExec
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.tasks.Copy

import java.util.regex.Pattern

/**
 * A gradle plugin that wraps pyenv, for easy use of python in gradle projects
 */
class PyenvPlugin implements Plugin<Project> {

    private Project project
    private PyenvExtension extension

    @Override
    void apply(Project project) {
        this.project = project
        this.extension = project.extensions.create('pyenv', PyenvExtension, project)

        configurePyenvDependency()
        createDefaultTasks()
        extendWithTaskTypes()
    }

    /**
     * Configures pyenv as an ivy dependency.
     *
     * Creates a dependency configuration named pyenv,
     * adds a repository for github releases and
     * adds pyenv as a dependency in the pyenv configuration.
     */
    void configurePyenvDependency() {
        project.repositories.ivy {
            url 'https://github.com/'
            layout 'pattern', {
                artifact '/[organisation]/[module]/archive/[revision].[ext]'
            }
        }
        project.configurations.create('pyenv')
        project.dependencies.add('pyenv', 'pyenv:pyenv:v1.2.4@zip')
    }

    void createDefaultTasks() {
        project.tasks.create('extractPythonBuild', Copy, this.&configureExtractPythonBuildTask)
        project.tasks.create('buildPython', BuildPython, this.&configureDefaultBuildPythonTask)
        project.tasks.create('createVenv', CreateVenv, this.&configureDefaultCreateVenvTask)
    }

    void extendWithTaskTypes() {
        extendWithTaskType(BuildPython)
        extendWithTaskType(CreateVenv)
        extendWithTaskType(VenvExec)
    }

    void extendWithTaskType(Class type) {
        project.extensions.extraProperties.set(type.simpleName, type)
    }

    static final Pattern stripParentPattern = Pattern.compile($/^pyenv-[0-9.]+//$)
    static final Pattern relevantFilePattern = Pattern.compile($/^plugins/python-build/(?:share/.*|bin/python-build)/$)
    static final Pattern stripPluginsDirPattern = Pattern.compile($/^plugins/python-build//$)

    void configureExtractPythonBuildTask(Copy task) {
        task.setDescription('Resolves pyenv through gradle dependency, and extracts python-build')
        task.from(project.zipTree(selectPyenvFile(project.configurations.pyenv.resolve())))
        task.into("${ -> extension.pythonBuildDirectoryProp.get()}")
        task.eachFile({ details ->
            assert details instanceof FileCopyDetails

            details.path = stripParentPattern.matcher(details.path).replaceFirst('')
            if (!relevantFilePattern.matcher(details.path).matches()) {
                details.exclude()
            } else {
                details.path = stripPluginsDirPattern.matcher(details.path).replaceFirst('')
            }
        })
    }

    void configureDefaultBuildPythonTask(BuildPython task) {
        task.setDescription('Builds python using python-build. Typically takes over 10 minutes')
        task.dependsOn(project.tasks['extractPythonBuild'])

        task.pythonBuildDirProp.set(extension.pythonBuildDirectoryProp)
        task.pythonProp.set(extension.pythonVersionProp)
        task.targetProp.set(extension.prefixDirectoryProp)
    }

    void configureDefaultCreateVenvTask(CreateVenv task) {
        task.setDescription('Create a virtual python environment with venv')
        task.dependsOn(project.tasks['buildPython'])

        task.prefixProp.set(extension.prefixDirectoryProp)
        task.targetProp.set(extension.environmentProp)
    }

    static File selectPyenvFile(Set<File> files) {
        return files.max({ a , b -> (a.name <=> b.name) })
    }

}
