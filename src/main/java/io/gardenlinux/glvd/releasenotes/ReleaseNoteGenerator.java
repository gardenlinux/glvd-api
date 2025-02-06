package io.gardenlinux.glvd.releasenotes;

import io.gardenlinux.glvd.GardenLinuxVersion;
import io.gardenlinux.glvd.db.DebSrc;
import io.gardenlinux.glvd.db.SourcePackageCve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ReleaseNoteGenerator {
    private final GardenLinuxVersion gardenLinuxVersion;
    private final List<SourcePackageCve> cvesOldVersion;
    private final List<SourcePackageCve> cvesNewVersion;
    private final List<String> resolvedInNew;
    private final List<DebSrc> sourcePackagesInOldVersion;
    private final List<DebSrc> sourcePackagesInNewVersion;

    public ReleaseNoteGenerator(GardenLinuxVersion gardenLinuxVersion, List<SourcePackageCve> cvesOldVersion, List<SourcePackageCve> cvesNewVersion, List<String> resolvedInNew, List<DebSrc> sourcePackagesInOldVersion, List<DebSrc> sourcePackagesInNewVersion) {
        this.gardenLinuxVersion = gardenLinuxVersion;
        this.cvesOldVersion = cvesOldVersion;
        this.cvesNewVersion = cvesNewVersion;
        this.resolvedInNew = resolvedInNew;
        this.sourcePackagesInOldVersion = sourcePackagesInOldVersion;
        this.sourcePackagesInNewVersion = sourcePackagesInNewVersion;
    }

    private String getVersionByPackageName(List<DebSrc> input, String packageName) {
        for (DebSrc debSrc : input) {
            if (debSrc.getDebSource().equalsIgnoreCase(packageName)) {
                return debSrc.getDebVersion();
            }
        }
        return "";
    }

    public ReleaseNote generate() {
        var cvesNewVersionIgnoreResolved = cvesNewVersion.stream().filter(sourcePackageCve -> !resolvedInNew.contains(sourcePackageCve.getCveId())).toList();
        var cvesNewVersionCveIds = cvesNewVersionIgnoreResolved.stream().map(SourcePackageCve::getCveId).collect(Collectors.joining());
        var diff = cvesOldVersion.stream().filter(sourcePackageCve -> !cvesNewVersionCveIds.contains(sourcePackageCve.getCveId())).toList();
        HashMap<String, List<String>> sourcePackageNameToCveListMapping = new HashMap<>();
        for (SourcePackageCve sourcePackageCve : diff) {
            var cveList = sourcePackageNameToCveListMapping.getOrDefault(sourcePackageCve.getSourcePackageName(), new ArrayList<>());
            cveList.add(sourcePackageCve.getCveId());
            sourcePackageNameToCveListMapping.put(sourcePackageCve.getSourcePackageName(), cveList);
        }
        List<ReleaseNotesPackage> releaseNotesPackages = new ArrayList<>();
        sourcePackageNameToCveListMapping.forEach((sourcePackage, cves) ->
                releaseNotesPackages.add(
                        new ReleaseNotesPackage(sourcePackage,
                                getVersionByPackageName(sourcePackagesInOldVersion, sourcePackage),
                                getVersionByPackageName(sourcePackagesInNewVersion, sourcePackage),
                                cves)
                )
        );

        return new ReleaseNote(gardenLinuxVersion.printVersion(), releaseNotesPackages);
    }
}
