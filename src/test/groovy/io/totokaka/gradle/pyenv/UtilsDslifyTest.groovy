package io.totokaka.gradle.pyenv

import org.gradle.api.internal.provider.DefaultPropertyState
import org.gradle.api.provider.Property
import spock.lang.Specification

class UtilsDslifyTest extends Specification {

    ClassToTest underTest

    void setup() {
        Utils.dslify(ClassToTest, 'testedProperty', 'tested')
        underTest = new ClassToTest()
    }

    def "test explicit setter"() {
        when:
        underTest.setTested('We are testing')

        then:
        underTest.testedProperty.get() == 'We are testing'
    }

    def "test implicit setter"() {
        when:
        underTest.tested = 'We are still testing'

        then:
        underTest.testedProperty.get() == 'We are still testing'
    }

    def "test setter method"() {
        when:
        underTest.tested 'are still testing'

        then:
        underTest.testedProperty.get() == 'are still testing'
    }

    def "test explicit getter"() {
        when:
        underTest.testedProperty.set('still testing')

        then:
        underTest.getTested() == 'still testing'
    }

    def "test implicit getter"() {
        when:
        underTest.testedProperty.set('testing')

        then:
        underTest.tested == 'testing'
    }

    class ClassToTest {
        Property<String> testedProperty = new DefaultPropertyState<>(String)
    }
}
