package io.totokaka.gradle.pyenv

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DirectoryChecksumUtilTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    void setup() {
        temporaryFolder.newFile() << "A file"
        new File(temporaryFolder.newFolder(), "child") << "A child"
    }

    def "ignores __pycache__ files and directories"() {
        given: "a checksum for a directory"
        byte[] before = DirectoryChecksumUtil.checksumDirectory(temporaryFolder.root)

        when: "files that should be ignored are added to the directory"
        new File(temporaryFolder.newFolder('__pycache__'), 'child') << "ignored"
        temporaryFolder.newFile('__pycache__.text') << "Also ignored"

        then: "the checksum should still be verifed true"
        DirectoryChecksumUtil.verifyDirectoryChecksum(temporaryFolder.root, before, '__pycache__')
    }

    def "VerifyDirectoryChecksum"() {
        when: "a checksum is calculated for a directory"
        def checksum = DirectoryChecksumUtil.checksumDirectory(temporaryFolder.root)

        then: "it should correctly verify"
        DirectoryChecksumUtil.verifyDirectoryChecksum(temporaryFolder.root, checksum)
    }

    def "ChecksumDirectory"() {
        when: "a checksum is calculated for a directory"
        def bytes = DirectoryChecksumUtil.checksumDirectory(temporaryFolder.root)

        then: "the checksum should not be null or have 0 length"
        bytes != null
        bytes.length > 0
    }

    def "ReadAndVerifyDirectoryChecksum and ChecksumDirectoryAndWrite"() {
        when: "a checksum is written for a directory"
        DirectoryChecksumUtil.checksumDirectoryAndWrite(temporaryFolder.root)

        then: "it should verify correctly"
        DirectoryChecksumUtil.readAndVerifyDirectoryChecksum(temporaryFolder.root)
    }

    def "ReadAndVerifyDirectoryChecksum and ChecksumDirectoryAndWrite inverted"() {
        given: "a written directory checksum"
        DirectoryChecksumUtil.checksumDirectoryAndWrite(temporaryFolder.root)

        when: "a new file is added"
        temporaryFolder.newFile() << 'something'

        then: "the checksum no longer matches"
        !DirectoryChecksumUtil.readAndVerifyDirectoryChecksum(temporaryFolder.root)
    }
}
