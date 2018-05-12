package io.totokaka.gradle.pyenv

import org.gradle.api.Project
import org.gradle.api.provider.Property

class PyenvExtension {
    /**
     * The directory pyenv will be built in
     */
    Property<File> bootstrapDirectoryProp
    Property<File> prefixDirectoryProp
    Property<File> pythonBuildDirectoryProp
    Property<String> pythonVersionProp
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

}
