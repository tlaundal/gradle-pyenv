package io.totokaka.gradle.pyenv

import io.totokaka.gradle.pyenv.tasks.BuildPython
import io.totokaka.gradle.pyenv.tasks.CreateVenv
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
        createExtractPythonBuildTask()
        createBuildPythonTask()
        createCreateVenvTask()
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

    /**
     * Create a task called extractPyenv on the project
     *
     * This task will extract the newest pyenv dependency.
     */
    void createExtractPythonBuildTask() {
        File archive = project.configurations.pyenv
                .resolve()
                .max({ a , b -> (a.name <=> b.name) })

        Pattern stripParentPattern = Pattern.compile($/^pyenv-[0-9.]+//$)
        Pattern relevantFilePattern = Pattern.compile($/^plugins/python-build/(?:share/.*|bin/python-build)/$)
        Pattern stripPluginsDirPattern = Pattern.compile($/^plugins/python-build//$)

        project.tasks.create('extractPythonBuild', Copy, { task ->
            task.from(project.zipTree(archive))
            task.into("${ -> extension.pythonBuildDirectory.get()}")
            task.eachFile({ details ->
                assert details instanceof FileCopyDetails

                details.path = stripParentPattern.matcher(details.path).replaceFirst('')
                if (!relevantFilePattern.matcher(details.path).matches()) {
                    details.exclude()
                } else {
                    details.path = stripPluginsDirPattern.matcher(details.path).replaceFirst('')
                }
            })
        })
    }

    void createBuildPythonTask() {
        project.tasks.create('buildPython', BuildPython, { task ->
            task.dependsOn(project.tasks['extractPythonBuild'])

            task.pythonBuildDir.set(extension.pythonBuildDirectory)
            task.python.set(extension.pythonVersion)
            task.target.set(extension.prefixDirectory)
        })
    }

    void createCreateVenvTask() {
        project.tasks.create('createVenv', CreateVenv, { task ->
            task.dependsOn(project.tasks['buildPython'])

            task.prefix.set(extension.prefixDirectory)
            task.target.set(extension.environment)
        })
    }

}
