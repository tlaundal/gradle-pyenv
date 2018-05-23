package io.totokaka.gradle.pyenv

import org.gradle.api.internal.provider.ProviderInternal
import org.gradle.internal.Cast
import spock.lang.Specification

class SupplierProviderTest extends Specification {

    def "Can be cast to InternalProvider"() {
        setup:
        def sp = SupplierProvider.of({"Hello World!"})

        when:
        ProviderInternal<String> p = Cast.uncheckedCast(sp)

        then:
        notThrown(ClassCastException)
    }

}
