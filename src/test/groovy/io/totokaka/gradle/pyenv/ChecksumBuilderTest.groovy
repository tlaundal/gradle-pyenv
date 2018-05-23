package io.totokaka.gradle.pyenv

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import spock.lang.Specification

import java.nio.file.Files

class ChecksumBuilderTest extends Specification {

    static final def HW = "Hello World!"
    static final byte[] HWB = [-19, 7, 98, -121, 83, 46, -122, 54, 94, -124, 30, -110, -65, -59, 13, -116]

    def fs = Jimfs.newFileSystem(Configuration.unix())
    def cd = ChecksumBuilder.md5()

    def "update reads bytes from String"() {
        when:
        cd.digest(HW)

        then:
        Arrays.equals(cd.checksum(), HWB)
    }

    def "update reads bytes from stream"() {
        setup:
        def stream = new ByteArrayInputStream(HW.getBytes())

        when:
        cd.digest(stream)

        then:
        Arrays.equals(cd.checksum(), HWB)
    }

    def "multiple updates can be used"() {
        when:
        cd.digest(HW.substring(0, 6))
        cd.digest(new ByteArrayInputStream(HW.substring(6, HW.length()).getBytes()))

        then:
        Arrays.equals(cd.checksum(), HWB)
    }

    def "can read files"() {
        setup:
        def file = fs.getPath("/file")
        Files.write(file, HW.getBytes())

        when:
        cd.digest(file)

        then:
        Arrays.equals(cd.checksum(), HWB)
    }

    def "can read directories"() {
        setup:
        def dir = fs.getPath("/dir")
        Files.createDirectory(dir)
        Files.write(dir.resolve("a"), HW.substring(0, 6).getBytes())
        Files.write(dir.resolve("b"), HW.substring(6, HW.length()).getBytes())

        when:
        cd.digest(dir)

        then:
        Arrays.equals(cd.checksum(), HWB)
    }

    def "can ignore files"() {
        setup:
        def dir = fs.getPath("/dir")
        Files.createDirectory(dir)
        Files.write(dir.resolve("hello-world.txt"), HW.getBytes())
        Files.write(dir.resolve("hello-world.bak"), HW.getBytes())

        when:
        cd.ignore(fs.getPathMatcher("glob:**.bak"))
        cd.digest(dir)

        then:
        Arrays.equals(cd.checksum(), HWB)
    }

}
