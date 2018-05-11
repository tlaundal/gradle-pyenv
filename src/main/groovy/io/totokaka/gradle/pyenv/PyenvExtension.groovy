package io.totokaka.gradle.pyenv

import org.gradle.api.Project
import org.gradle.api.provider.Property

class PyenvExtension {
    /**
     * The directory pyenv will be built in
     */
    Property<File> bootstrapDirectory
    Property<File> prefixDirectory
    Property<File> pythonBuildDirectory
    Property<String> pythonVersion
    Property<File> environment

    PyenvExtension(Project project) {
        bootstrapDirectory = project.objects.property(File)
        prefixDirectory = project.objects.property(File)
        pythonBuildDirectory = project.objects.property(File)
        pythonVersion = project.objects.property(String)
        environment = project.objects.property(File)

        bootstrapDirectory.set(new File("${project.rootDir}/.gradle/pyenv-bootstrap"))
        prefixDirectory.set(bootstrapDirectory.map({ new File(it, 'prefix') }))
        pythonBuildDirectory.set(bootstrapDirectory.map({ new File(it, 'python-build')}))
        pythonVersion.set('3.6.0')
        environment.set(bootstrapDirectory.map({ new File(it, 'env') }))
    }

}
