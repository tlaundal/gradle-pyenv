package io.totokaka.gradle.pyenv;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ChecksumBuilder {

    private final MessageDigest digest;
    private final Set<PathMatcher> ignore;

    public ChecksumBuilder(MessageDigest digest) {
        this.digest = digest;
        ignore = new HashSet<>();
    }

    public static ChecksumBuilder md5() throws NoSuchAlgorithmException {
        return new ChecksumBuilder(MessageDigest.getInstance("MD5"));
    }

    /**
     * Add a rule for files that are ignored.
     *
     * Ignore rules only affects children resolved when using
     * {@link ChecksumBuilder#digest(Path)} with a directory as the argument.
     *
     * @param matcher A matcher for files that should be ignored.
     * @return for chaining
     */
    public ChecksumBuilder ignore(PathMatcher matcher) {
        this.ignore.add(matcher);

        return this;
    }

    public ChecksumBuilder digest(InputStream stream) throws IOException {
        byte[] buffer = new byte[1024];
        for (int read = stream.read(buffer, 0, buffer.length); read > -1;
                 read = stream.read(buffer, 0, buffer.length)) {
            this.digest.update(buffer, 0, read);
        }
        return this;
    }

    public ChecksumBuilder digest(String string) {
        this.digest.update(string.getBytes());
        return this;
    }

    public ChecksumBuilder digest(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.list(path)
                    .filter(child -> this.ignore.stream()
                            .noneMatch(rule -> rule.matches(child)))
                    .sorted(Comparator.comparing(Path::toString))
                    .forEachOrdered(unchecked(this::digest));
        } else if (Files.isRegularFile(path)) {
            try (InputStream stream = Files.newInputStream(path)) {
                this.digest(stream);
            }
        }

        return this;
    }

    public byte[] checksum() {
        return this.digest.digest();
    }

    private <T> Consumer<T> unchecked(ThrowingConsumer<T, ?> consumer) {
        return arg -> {
            try {
                consumer.consume(arg);
            } catch (Exception ex) {
                if (ex instanceof IOException) {
                    throw new UncheckedIOException((IOException)ex);
                } else {
                    throw new RuntimeException(ex);
                }
            }
        };
    }

    private interface ThrowingConsumer<T, E extends Exception> {
        void consume(T arg) throws E;
    }

}
