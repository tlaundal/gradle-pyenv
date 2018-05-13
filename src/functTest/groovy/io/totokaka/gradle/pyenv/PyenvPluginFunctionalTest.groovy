package io.totokaka.gradle.pyenv

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class PyenvPluginFunctionalTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'io.totokaka.gradle.pyenv'
            }
            
            task pythonHello(type: VenvExec) {
                dependsOn createVenv
                executable 'python'
                arguments '-c', 'print("Hello World!")'
                
            }
        """

    }

    def "can run python in venv"() {
        setup:
        def runner = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('--stacktrace', '--debug', 'pythonHello')
                .withPluginClasspath()

        when:
        def result = runner.build()

        then:
        result.task(":pythonHello").outcome == SUCCESS
        result.output.contains('Hello World!')
    }

}
