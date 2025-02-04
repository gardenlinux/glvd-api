package io.gardenlinux.glvd;

import java.util.List;

public class ReleaseNotesPackage {
    private final String sourcePackageName;
    private final String oldVersion;
    private final String newVersion;
    private final List<String> fixedCves;

    public ReleaseNotesPackage(String sourcePackageName, String oldVersion, String newVersion, List<String> fixedCves) {
        this.sourcePackageName = sourcePackageName;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.fixedCves = fixedCves;
    }

    public String getSourcePackageName() {
        return sourcePackageName;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public List<String> getFixedCves() {
        return fixedCves;
    }

    @Override
    public String toString() {
        return "ReleaseNotesPackage{" +
                "sourcePackageName='" + sourcePackageName + '\'' +
                ", oldVersion='" + oldVersion + '\'' +
                ", newVersion='" + newVersion + '\'' +
                ", fixedCves=" + fixedCves +
                '}';
    }
}
