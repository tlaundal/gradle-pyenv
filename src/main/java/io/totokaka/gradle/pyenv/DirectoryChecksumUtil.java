package io.totokaka.gradle.pyenv;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class DirectoryChecksumUtil {

    public static boolean verifyDirectoryChecksum(File directory, byte[] checksum) {
        try {
            byte[] actual = checksumDirectory(directory);
            return Arrays.equals(checksum, actual);
        } catch (NoSuchAlgorithmException|IOException ignored) {
            return false;
        }
    }

    public static byte[] checksumDirectory(File directory) throws NoSuchAlgorithmException, IOException {
        if (!directory.isDirectory()) {
            throw new UnsupportedOperationException(directory.toString() + " is not a directory");
        }

        List<InputStream> streams = new ArrayList<>();
        collectInputStreams(streams, directory);

        MessageDigest md = MessageDigest.getInstance("MD5");
        InputStream sequence = streams.stream().reduce(SequenceInputStream::new).orElse(null);

        if (sequence != null) {
            byte[] buffer = new byte[1024];
            for (int read = sequence.read(buffer, 0, 1024); read > -1; read = sequence.read(buffer, 0, 1024)) {
                md.update(buffer, 0, read);
            }
        }

        for (InputStream is : streams) {
            is.close();
        }

        return md.digest();
    }

    private static void collectInputStreams(List<InputStream> streams, File directory) {
        streamIfNotNull(directory.listFiles(File::isDirectory))
                .sorted(Comparator.comparing(File::getAbsolutePath))
                .forEachOrdered(d -> collectInputStreams(streams, d));

        streamIfNotNull(directory.listFiles(File::isFile))
                .sorted(Comparator.comparing(File::getAbsolutePath))
                .flatMap(SilencingStreamFlatMapper.of(FileInputStream::new))
                .forEachOrdered(streams::add);
    }

    private static <T> Stream<T> streamIfNotNull(T[] elements) {
        if (elements == null) {
            return Stream.empty();
        }
        return Arrays.stream(elements);
    }

    private interface SilencingStreamFlatMapper<T, R> extends Function<T, Stream<R>> {

        R applyMayThrow(T t) throws Exception;

        @Override
        default Stream<R> apply(T t) {
            try {
                return Stream.of(this.applyMayThrow(t));
            } catch (Exception ignored) {
                return Stream.empty();
            }
        }

        static <T, R> SilencingStreamFlatMapper<T, R> of(SilencingStreamFlatMapper<T, R> func) {
            return func;
        }
    }

    private static class IteratorEnumeration<T> implements Enumeration<T> {
        Iterator<T> iterator;

        IteratorEnumeration(Iterable<T> iterable) {
            this.iterator = iterable.iterator();
        }

        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public T nextElement() {
            return iterator.next();
        }
    }

}
