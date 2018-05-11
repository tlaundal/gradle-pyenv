package io.totokaka.gradle.pyenv

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Path

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
        """
    }

    def "can download and extract pyenv"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('extractPythonBuild')
                .withPluginClasspath()
                .build()

        then:
        result.task(":extractPythonBuild").outcome == SUCCESS
        new File(testProjectDir.root, '.gradle/pyenv-bootstrap/python-build/bin/python-build').isFile()
    }

}
