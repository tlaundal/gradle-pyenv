package io.totokaka.gradle.pyenv;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class DirectoryChecksumUtil {

    /**
     * Resolve the checksum file for this directory.
     *
     * This resolves a file with the same name as the directory, but appended
     * ".checksum" as a sibling to the directory.
     *
     * @param directory The directory to resolve a checksum file for
     * @return The checksum file for the directory
     */
    public static Path resolveChecksum(Path directory) {
        return directory.resolveSibling(directory.getFileName() + ".checksum");
    }

    /**
     * Verify the checksum of a directory.
     *
     * Will read the checksum file for the directory and compare it to the
     * supplied checksum. Will short circuit to false if:
     * <ul>
     *     <li>The directory is not a directory</li>
     *     <li>The checksum file for the directory is not a normal file</li>
     * </ul>
     *
     * @param directory The directory to verify the checksum of
     * @param checksum The expected checksum
     * @return Whether the checksums matches
     */
    public static boolean verifyDirectory(Path directory, byte[] checksum) {
        Path checksumFile = resolveChecksum(directory);
        try {
            return Files.isDirectory(directory) &&
                    Files.isRegularFile(checksumFile) &&
                    Arrays.equals(Files.readAllBytes(checksumFile), checksum);
        } catch (IOException e) {
            return false;
        }
    }

}
