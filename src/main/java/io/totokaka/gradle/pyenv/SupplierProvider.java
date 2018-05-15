package io.totokaka.gradle.pyenv;

import org.gradle.api.Transformer;
import org.gradle.api.provider.Provider;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface SupplierProvider<T> extends Provider<T>, Supplier<T> {

    @Nullable
    @Override
    default T getOrNull() {
        return this.getOrElse(null);
    }

    @Override
    default T getOrElse(T defaultValue) {
        try {
            return this.get();
        } catch (IllegalStateException ignored) {
            return defaultValue;
        }
    }

    @Override
    default <S> Provider<S> map(Transformer<? extends S, ? super T> transformer) {
        return of(() -> transformer.transform(this.get()));
    }

    @Override
    default boolean isPresent() {
        return true;
    }

    static <T> SupplierProvider<T> of (SupplierProvider<T> provider) {
        return provider;
    }
}
