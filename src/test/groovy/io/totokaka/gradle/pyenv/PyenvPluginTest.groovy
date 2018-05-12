package io.totokaka.gradle.pyenv

import spock.lang.Specification

class PyenvPluginTest extends Specification {
    def "SelectPyenvFile selects newest pyenv"() {
        setup:
        File a = new File('pyenv-v1.0.3.zip')
        File b = new File('pyenv-v1.2.4.zip')
        File c = new File('pyenv-v1.1.4.zip')

        when:
        File selected = PyenvPlugin.selectPyenvFile([a, b, c].toSet())

        then:
        selected == b
    }
}
