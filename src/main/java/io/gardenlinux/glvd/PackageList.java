package io.gardenlinux.glvd;

import java.util.List;

public class PackageList {

    private List<String> packageNames;

    public PackageList() {
    }

    public PackageList(List<String> packageNames) {
        this.packageNames = packageNames;
    }

    public List<String> getPackageNames() {
        return packageNames;
    }

    @Override
    public String toString() {
        return String.join(",", packageNames);
    }
}
