package io.gardenlinux.glvd.releasenotes;

import io.gardenlinux.glvd.db.SourcePackageCve;

import java.util.List;
import java.util.stream.Collectors;

public class CveDiffer {
    private final List<SourcePackageCve> cvesOldVersion;
    private final List<SourcePackageCve> cvesNewVersion;
    private final List<String> resolvedInNew;

    public CveDiffer(List<SourcePackageCve> cvesOldVersion, List<SourcePackageCve> cvesNewVersion, List<String> resolvedInNew) {
        this.cvesOldVersion = cvesOldVersion;
        this.cvesNewVersion = cvesNewVersion;
        this.resolvedInNew = resolvedInNew;
    }

    List<SourcePackageCve> diff() {
        var cvesNewVersionIgnoreResolved = cvesNewVersion.stream().filter(sourcePackageCve -> !resolvedInNew.contains(sourcePackageCve.getCveId())).toList();
        var cvesNewVersionCveIds = cvesNewVersionIgnoreResolved.stream().map(SourcePackageCve::getCveId).collect(Collectors.joining());
        return cvesOldVersion.stream().filter(sourcePackageCve -> !cvesNewVersionCveIds.contains(sourcePackageCve.getCveId())).toList();
    }
}
