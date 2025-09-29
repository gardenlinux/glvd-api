package io.gardenlinux.glvd.version;

@Deprecated()
public class TwoDigitGardenLinuxVersion implements GardenLinuxVersion {
    private final String version;
    private final int major;
    private final int patch;

    public TwoDigitGardenLinuxVersion(String version) {
        this.version = version;
        var components = version.split("\\.");
        if (components.length != 2) {
            throw new IllegalArgumentException("Expected version number in format (major as int).(patch as int)");
        }
        major = Integer.parseInt(components[0]);
        patch = Integer.parseInt(components[1]);
    }

    public String previousPatchVersion() {
        return major + "." + (patch - 1);
    }

    public String printVersion() {
        return version;
    }
}
