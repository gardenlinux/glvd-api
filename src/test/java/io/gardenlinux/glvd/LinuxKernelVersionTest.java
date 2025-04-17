package io.gardenlinux.glvd;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinuxKernelVersionTest {

    @Test
    void testFromRawVersion() {
        LinuxKernelVersion version = LinuxKernelVersion.fromRawVersion("6.12.23-something");
        assertEquals("LinuxKernelVersion{rawVersion='6.12.23-something', major=6, minor=12, patch=23}", version.toString());
        assertEquals(6, version.major);
        assertEquals(12, version.minor);
        assertEquals(23, version.patch);
    }

    @Test
    void testParseRawVersion() {
        List<Integer> components = LinuxKernelVersion.parseRawVersion("6.12.23-something");
        assertEquals(List.of(6, 12, 23), components);
    }

    @Test
    void testParseRawVersionNoPatchVersion() {
        List<Integer> components = LinuxKernelVersion.parseRawVersion("6.12");
        assertEquals(List.of(6, 12), components);
    }

    @Test
    void testParseRawVersionFourParts() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            LinuxKernelVersion.parseRawVersion("6.12.23.42");
        });
        assertEquals("Expected 6.12.23.42 to follow the format of 6.12 or 6.12.23 or 6.12.23-somethingoptional", exception.getMessage());
    }

    @Test
    void testParseRawVersionInvalidFormat() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            LinuxKernelVersion.parseRawVersion("invalid-version");
        });
        assertEquals("Expected invalid-version to follow the format of 6.12 or 6.12.23 or 6.12.23-somethingoptional", exception.getMessage());
    }

    @Test
    void testCompareTo() {
        LinuxKernelVersion v1 = LinuxKernelVersion.fromRawVersion("6.12.23");
        LinuxKernelVersion v2 = LinuxKernelVersion.fromRawVersion("6.12.24");
        LinuxKernelVersion v3 = LinuxKernelVersion.fromRawVersion("6.13.0");
        LinuxKernelVersion v4 = LinuxKernelVersion.fromRawVersion("7.0.0");

        assertTrue(v1.compareTo(v2) < 0); // v1 < v2
        assertTrue(v2.compareTo(v1) > 0); // v2 > v1
        assertTrue(v1.compareTo(v1) == 0); // v1 == v1
        assertTrue(v2.compareTo(v3) < 0); // v2 < v3
        assertTrue(v3.compareTo(v4) < 0); // v3 < v4
    }

    @Test
    void testToString() {
        LinuxKernelVersion version = LinuxKernelVersion.fromRawVersion("6.12.23");
        String expected = "LinuxKernelVersion{rawVersion='6.12.23', major=6, minor=12, patch=23}";
        assertEquals(expected, version.toString());
    }

    @Test
    void testToStringNullable() {
        LinuxKernelVersion version = LinuxKernelVersion.fromRawVersion("6.12");
        String expected = "LinuxKernelVersion{rawVersion='6.12', major=6, minor=12, patch=null}";
        assertEquals(expected, version.toString());
    }


    @Test
    void testToStringWithDash() {
        LinuxKernelVersion version = LinuxKernelVersion.fromRawVersion("6.12.23-0gl0~bp1592");
        String expected = "LinuxKernelVersion{rawVersion='6.12.23-0gl0~bp1592', major=6, minor=12, patch=23}";
        assertEquals(expected, version.toString());
    }
}
