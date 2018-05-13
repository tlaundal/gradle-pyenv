package io.totokaka.gradle.pyenv

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DirectoryChecksumUtilTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    void setup() {
        testProjectDir.newFile() << "A file"
        new File(testProjectDir.newFolder(), "child") << "A child"
    }

    def "VerifyDirectoryChecksum"() {
        setup:
        def checksum = DirectoryChecksumUtil.checksumDirectory(testProjectDir.root)

        when:
        def matches = DirectoryChecksumUtil.verifyDirectoryChecksum(testProjectDir.root, checksum)

        then:
        matches
    }

    def "ChecksumDirectory"() {
        when:
        def bytes = DirectoryChecksumUtil.checksumDirectory(testProjectDir.root)

        then:
        bytes != null
        bytes.length > 0
    }
}
