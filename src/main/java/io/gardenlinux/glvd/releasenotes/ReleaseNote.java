package io.gardenlinux.glvd.releasenotes;

import java.util.List;

public class ReleaseNote {
    private final String version;
    private final List<ReleaseNotesPackage> packageList;

    public ReleaseNote(String version, List<ReleaseNotesPackage> packageList) {
        this.version = version;
        this.packageList = packageList;
    }

    public String getVersion() {
        return version;
    }

    public List<ReleaseNotesPackage> getPackageList() {
        return packageList;
    }
}
