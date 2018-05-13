package io.totokaka.gradle.pyenv

final class Utils {
    
    static void dslify(Class type, String prop, String alias) {
        def getter = { type.metaClass.getAttribute(type, delegate, prop, false).get() }
        def setter = { type.metaClass.getAttribute(type, delegate, prop, false).set(it) }
        type.metaClass["get${alias.capitalize()}"] = getter
        type.metaClass["set${alias.capitalize()}"] = setter
        type.metaClass[alias] = setter
    }

}
