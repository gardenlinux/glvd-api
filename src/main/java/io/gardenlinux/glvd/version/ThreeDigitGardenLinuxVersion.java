package io.gardenlinux.glvd.version;

public class ThreeDigitGardenLinuxVersion implements GardenLinuxVersion {
    private final String version;
    private final int major;
    private final int minor;
    private final int patch;

    public ThreeDigitGardenLinuxVersion(String version) {
        this.version = version;
        var components = version.split("\\.");
        if (components.length != 3) {
            throw new IllegalArgumentException("Expected version number in format (major as int).(minor as int).(patch as int)");
        }
        major = Integer.parseInt(components[0]);
        minor = Integer.parseInt(components[1]);
        patch = Integer.parseInt(components[2]);
    }

    public String previousMinorVersion() {
        return major + "." + (minor - 1) + "." + patch;
    }

    public String printVersion() {
        return version;
    }
}
