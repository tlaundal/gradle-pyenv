package io.totokaka.gradle.pyenv

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class DirectoryChecksumUtilTest extends Specification {
    def "ResolveChecksum"() {
        expect:
        DirectoryChecksumUtil.resolveChecksum(Paths.get("alpha").resolve("bravo")) ==
                Paths.get("alpha").resolve("bravo.checksum")
    }

    def "VerifyDirectory"() {
        setup:
        byte[] bytes = [16, -23, 63, 2]
        def fs = Jimfs.newFileSystem(Configuration.unix())
        def dir = fs.getPath("testDir")
        Files.createDirectory(dir)
        Files.write(fs.getPath("testDir.checksum"), bytes)

        expect:
        DirectoryChecksumUtil.verifyDirectory(dir, bytes)
    }

    def "verifyDirectory doesn't throw"() {
        setup:
        def fs = Jimfs.newFileSystem(Configuration.unix())

        expect:
        !DirectoryChecksumUtil.verifyDirectory(fs.getPath("foo", "bar"), [0] as byte[])
    }
}
