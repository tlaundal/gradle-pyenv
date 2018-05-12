package io.totokaka.gradle.pyenv

import org.gradle.api.Project
import org.gradle.api.internal.provider.DefaultPropertyState
import org.gradle.api.model.ObjectFactory
import spock.lang.Specification

import java.nio.file.Paths

class PyenvExtensionTest extends Specification {

    File rootDir = new File('root')
    File buildDir = new File(rootDir, 'build')
    Project project = [
            getRootDir: { rootDir },
            getBuildDir: { buildDir },
            getObjects: { [ property: { Class type -> new DefaultPropertyState<>(type) } ] as ObjectFactory }
    ] as Project

    def "verify default values"() {
        when:
        PyenvExtension extension = new PyenvExtension(project)

        then:
        extension.bootstrapDirectoryProp.get().toPath() == rootDir.toPath().resolve('.gradle').resolve('pyenv-bootstrap')
        extension.prefixDirectoryProp.get().toPath() == extension.bootstrapDirectoryProp.get().toPath().resolve('prefix')
        extension.pythonBuildDirectoryProp.get().toPath() == extension.bootstrapDirectoryProp.get().toPath().resolve('python-build')
        extension.pythonVersionProp.get() == '3.6.0'
        extension.environmentProp.get().toPath() == buildDir.toPath().resolve('venv')
    }

    def "test DSL setter and getter methods"() {
        setup:
        PyenvExtension extension = new PyenvExtension(project)

        when:
        extension.with {
            bootstrapDirectory new File('custom', 'bootstrap')
            prefixDirectory new File('custom', 'prefix')
            pythonBuildDirectory new File('custom', 'python-build')
            pythonVersion '3.5.4'
            environment new File('custom', 'env')
        }

        then:
        extension.bootstrapDirectory.toPath() == Paths.get('custom', 'bootstrap')
        extension.prefixDirectory.toPath() == Paths.get('custom', 'prefix')
        extension.pythonBuildDirectory.toPath() == Paths.get('custom', 'python-build')
        extension.pythonVersion == '3.5.4'
        extension.environment.toPath() == Paths.get('custom', 'env')
    }

    def "test implicit setters"() {
        setup:
        PyenvExtension extension = new PyenvExtension(project)

        when:
        extension.with {
            bootstrapDirectory = new File('custom', 'bootstrap')
            prefixDirectory = new File('custom', 'prefix')
            pythonBuildDirectory = new File('custom', 'python-build')
            pythonVersion = '3.5.4'
            environment = new File('custom', 'env')
        }

        then:
        extension.bootstrapDirectoryProp.get().toPath() == Paths.get('custom', 'bootstrap')
        extension.prefixDirectoryProp.get().toPath() == Paths.get('custom', 'prefix')
        extension.pythonBuildDirectoryProp.get().toPath() == Paths.get('custom', 'python-build')
        extension.pythonVersionProp.get() == '3.5.4'
        extension.environmentProp.get().toPath() == Paths.get('custom', 'env')
    }

}
