package io.totokaka.gradle.pyenv.tasks

import spock.lang.Specification

class ExtractPythonBuildTest extends Specification {
    def "SelectPyenvFile selects newest pyenv"() {
        setup:
        File a = new File('pyenv-v1.0.3.zip')
        File b = new File('pyenv-v1.2.4.zip')
        File c = new File('pyenv-v1.1.4.zip')

        when:
        File selected = ExtractPythonBuild.selectPyenvFile([a, b, c].toSet())

        then:
        selected == b
    }
}
