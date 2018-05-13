package io.totokaka.gradle.pyenv

import org.gradle.api.Project
import org.gradle.api.provider.Property

class PyenvExtension {

    /**
     * The parent directory used for build tasks.
     * Used to set the defaults for prefixDirectory and pythonBuildDirectory.
     * Defaults to "${project.rootDir}/.gradle/pyenv-bootstrap"
     */
    Property<File> bootstrapDirectoryProp

    /**
     * The directory the built python will be placed into.
     * Defaults to "${project.rootDir}/.gradle/pyenv-bootstrap/prefix"
     */
    Property<File> prefixDirectoryProp

    /**
     * The directory pyenv's python-build will be extracted to.
     * Defaults to "${project.rootDir}/.gradle/pyenv-bootstrap/python-build"
     */
    Property<File> pythonBuildDirectoryProp

    /**
     * The python version to use.
     * Defaults to "3.6.0"
     */
    Property<String> pythonVersionProp

    /**
     * The directory the virtualenv will be placed in.
     * Defaults to "${project.buildDir}/venv"
     */
    Property<File> environmentProp

    PyenvExtension(Project project) {
        bootstrapDirectoryProp = project.objects.property(File)
        prefixDirectoryProp = project.objects.property(File)
        pythonBuildDirectoryProp = project.objects.property(File)
        pythonVersionProp = project.objects.property(String)
        environmentProp = project.objects.property(File)

        bootstrapDirectoryProp.set(new File("${project.rootDir}/.gradle/pyenv-bootstrap"))
        prefixDirectoryProp.set(bootstrapDirectoryProp.map({ new File(it, 'prefix') }))
        pythonBuildDirectoryProp.set(bootstrapDirectoryProp.map({ new File(it, 'python-build')}))
        pythonVersionProp.set('3.6.0')
        environmentProp.set(new File(project.buildDir, 'venv'))
    }

    static {
        Utils.dslify(PyenvExtension, 'bootstrapDirectoryProp', 'bootstrapDirectory')
        Utils.dslify(PyenvExtension, 'prefixDirectoryProp','prefixDirectory')
        Utils.dslify(PyenvExtension, 'pythonBuildDirectoryProp','pythonBuildDirectory')
        Utils.dslify(PyenvExtension, 'pythonVersionProp','pythonVersion')
        Utils.dslify(PyenvExtension, 'environmentProp','environment')
    }

    static PyenvExtension create(Project project) {
        return project.extensions.create("pyenv", PyenvExtension, project)
    }

}
