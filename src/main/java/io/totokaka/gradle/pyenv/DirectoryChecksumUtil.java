package io.totokaka.gradle.pyenv;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class DirectoryChecksumUtil {

    public static boolean verifyDirectoryChecksum(File directory, byte[] checksum, String... ignore) {
        try {
            byte[] actual = checksumDirectory(directory, ignore);
            return Arrays.equals(checksum, actual);
        } catch (NoSuchAlgorithmException|IOException ignored) {
            return false;
        }
    }

    public static byte[] checksumDirectory(File directory, String... ignore) throws NoSuchAlgorithmException, IOException {
        if (!directory.isDirectory()) {
            throw new UnsupportedOperationException(directory.toString() + " is not a directory");
        }

        List<InputStream> streams = new ArrayList<>();
        collectInputStreams(streams, directory, ignore);

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

    public static boolean readAndVerifyDirectoryChecksum(File directory, String... ignores) {
        return directory.isDirectory() &&
                DirectoryChecksumUtil.verifyDirectoryChecksum(
                        directory,
                        readFileBytes(getChecksumFile(directory)),
                        ignores);
    }


    public static void checksumDirectoryAndWrite(File directory, String... ignores) {
        try (FileOutputStream outputStream =
                     new FileOutputStream(getChecksumFile(directory))) {
            byte[] checksum = DirectoryChecksumUtil.checksumDirectory(
                    directory, ignores);
            outputStream.write(checksum);
        } catch (IOException | NoSuchAlgorithmException ignored) {}
    }

    private static File getChecksumFile(File directory) {
        return new File(directory.getParent(),
                directory.getName() + ".checksum");
    }

    private static boolean passesFilters(File file, String... ignores) {
        String path = file.getPath();
        for (String ignore : ignores) {
            if (path.contains(ignore)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Simple util method to safely read a file into a byte array.
     *
     * @param file The file to read
     * @return The bytes of the file, or an empty byte array if errors occurred
     *         while reading the file
     */
    private static byte[] readFileBytes(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            for (int read = inputStream.read(buffer, 0, 1024);
                 read >= 0;
                 read = inputStream.read(buffer, 0, 1024)) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException ignored) {
            return new byte[0];
        }
    }

    private static void collectInputStreams(List<InputStream> streams, File directory, String... ignore) {
        streamIfNotNull(directory.listFiles(File::isDirectory))
                .sorted(Comparator.comparing(File::getAbsolutePath))
                .filter(f -> passesFilters(f, ignore))
                .forEachOrdered(d -> collectInputStreams(streams, d));

        streamIfNotNull(directory.listFiles(File::isFile))
                .sorted(Comparator.comparing(File::getAbsolutePath))
                .filter(f -> passesFilters(f, ignore))
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

}
