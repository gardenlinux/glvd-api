package io.gardenlinux.glvd;

import java.util.Arrays;
import java.util.List;

public class LinuxKernelVersion implements Comparable<LinuxKernelVersion> {
    final int major;
    final int minor;
    final int patch;
    private final String rawVersion;


    private LinuxKernelVersion(String rawVersion, int major, int minor, int patch) {
        this.rawVersion = rawVersion;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    static LinuxKernelVersion fromRawVersion(String rawVersion) {
        var components = parseRawVersion(rawVersion);
        return new LinuxKernelVersion(rawVersion, components.get(0), components.get(1), components.get(2));
    }

    static List<Integer> parseRawVersion(String rawVersion) {
        // not all versions have a dash, but debian versions often do
        // we only care about the part before the dash to compare
        var componentsDash = rawVersion.split("-");
        var componentsDot = componentsDash[0].split("\\.");

        if (componentsDot.length != 3) {
            throw new RuntimeException("Expected " + rawVersion + " to follow the format of 6.12.23-somethingoptional");
        }

        return Arrays.stream(componentsDot).map(Integer::parseInt).toList();
    }

    @Override
    public int compareTo(LinuxKernelVersion o) {
        // Compare major versions first
        int majorComparison = Integer.compare(this.major, o.major);
        if (majorComparison != 0) {
            return majorComparison;
        }

        // If major versions are equal, compare minor versions
        int minorComparison = Integer.compare(this.minor, o.minor);
        if (minorComparison != 0) {
            return minorComparison;
        }

        // If minor versions are equal, compare patch versions
        return Integer.compare(this.patch, o.patch);
    }

    @Override
    public String toString() {
        return "LinuxKernelVersion{" +
                "rawVersion='" + rawVersion + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", patch=" + patch +
                '}';
    }
}
