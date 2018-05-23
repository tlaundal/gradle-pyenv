package io.totokaka.gradle.pyenv

import io.totokaka.gradle.pyenv.plugins.base.SimplePythonBasePlugin
import io.totokaka.gradle.pyenv.tasks.BuildPython
import io.totokaka.gradle.pyenv.tasks.CreateVenv
import io.totokaka.gradle.pyenv.tasks.VenvExec
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SimplePythonBaseFunctionalTest extends Specification {

    def project = ProjectBuilder.builder().withName("test-project").build()

    def setup() {
        project.pluginManager.apply(SimplePythonBasePlugin)
    }

    def "exports types"() {
        expect:
        with(project.extensions.extraProperties) {
            get("BuildPython") == BuildPython
            get("CreateVenv") == CreateVenv
            get("VenvExec") == VenvExec
        }
    }

    def "defines repo"() {
        expect:
        project.repositories.find { repo ->
            repo.name == 'github-releases'
        }
    }

    def "pyenv configuration resolves files"() {
        // It would be great if we could run this test without actually
        // downloading the dependencies, but it seems to be the only way to
        // apply the default dependencies
        when:
        def files = project.configurations.pyenv.resolve()

        then:
        files.size() > 0
    }

}
