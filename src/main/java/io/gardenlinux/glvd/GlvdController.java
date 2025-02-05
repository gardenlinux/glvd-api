package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.CveDetailsWithContext;
import io.gardenlinux.glvd.db.DebSrc;
import io.gardenlinux.glvd.db.SourcePackage;
import io.gardenlinux.glvd.db.SourcePackageCve;
import jakarta.annotation.Nonnull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class GlvdController {

    @Nonnull
    private final GlvdService glvdService;

    public GlvdController(@Nonnull GlvdService glvdService) {
        this.glvdService = glvdService;
    }

    @GetMapping("/cves/{gardenlinuxVersion}")
    ResponseEntity<List<SourcePackageCve>> getCveDistro(
            @PathVariable final String gardenlinuxVersion,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize
    ) {
        return ResponseEntity.ok().body(glvdService.getCveForDistribution(
                gardenlinuxVersion, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize))
        );
    }

    @GetMapping("/cves/{gardenlinuxVersion}/packages/{packageList}")
    ResponseEntity<List<SourcePackageCve>> getCvePackages(
            @PathVariable final String gardenlinuxVersion,
            @PathVariable final String packageList,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize
    ) {
        var cveForPackages = glvdService.getCveForPackages(
                gardenlinuxVersion, packageList, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)
        );
        return ResponseEntity.ok().body(cveForPackages);
    }

    @PutMapping("/cves/{gardenlinuxVersion}/packages")
    ResponseEntity<List<SourcePackageCve>> getCvePackagesxx(
            @PathVariable final String gardenlinuxVersion,
            @RequestBody final PackageList packages,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize
    ) {
        var packageList = packages.toString();
        var cveForPackages = glvdService.getCveForPackages(
                gardenlinuxVersion, packageList, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)
        );
        return ResponseEntity.ok().body(cveForPackages);
    }

    @GetMapping("/packages/{sourcePackage}")
    ResponseEntity<List<SourcePackageCve>> packageWithVulnerabilities(
            @PathVariable final String sourcePackage,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize
    ) {
        return ResponseEntity.ok(
                glvdService.getPackageWithVulnerabilities(sourcePackage, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize))
        );
    }

    @GetMapping("/packages/{sourcePackage}/{sourcePackageVersion}")
    ResponseEntity<List<SourcePackageCve>> packageWithVulnerabilitiesByVersion(
            @PathVariable final String sourcePackage,
            @PathVariable final String sourcePackageVersion,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize
    ) {
        return ResponseEntity.ok(
                glvdService.getPackageWithVulnerabilitiesByVersion(
                        sourcePackage, sourcePackageVersion, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)
                )
        );
    }

    @GetMapping("/distro/{gardenlinuxVersion}")
    ResponseEntity<List<SourcePackage>> packagesForDistro(
            @PathVariable final String gardenlinuxVersion,
            @RequestParam(defaultValue = "sourcePackageName") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize
    ) {
        return ResponseEntity.ok(
                glvdService.getPackagesForDistro(gardenlinuxVersion, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize))
        );
    }

    @GetMapping("/distro/{gardenlinuxVersion}/{cveId}")
    ResponseEntity<List<SourcePackageCve>> packagesByVulnerability(
            @PathVariable final String gardenlinuxVersion,
            @PathVariable final String cveId,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize
    ) {
        return ResponseEntity.ok(
                glvdService.getPackagesByVulnerability(gardenlinuxVersion, cveId, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize))
        );
    }

    @GetMapping("/cveDetails/{cveId}")
    ResponseEntity<CveDetailsWithContext> cveDetails(@PathVariable final String cveId) {
        var cveDetails = glvdService.getCveDetails(cveId);
        var cveContexts = glvdService.getCveContexts(cveId);

        return ResponseEntity.ok(new CveDetailsWithContext(cveDetails, cveContexts));
    }

    private String getVersionByPackageName(List<DebSrc> input, String packageName) {
        for (DebSrc debSrc : input) {
            if (debSrc.getDebSource().equalsIgnoreCase(packageName)) {
                return debSrc.getDebVersion();
            }
        }
        return "";
    }

    // https://github.com/gardenlinux/glvd/issues/132
    // Assumptions:
    //  - Not used for .0 versions
    //  - No burnt versions
    @GetMapping("/patchReleaseNotes/{gardenlinuxVersion}")
    ReleaseNote releaseNotes(@PathVariable final String gardenlinuxVersion) {
        if (gardenlinuxVersion.endsWith(".0")) {
            return new ReleaseNote(gardenlinuxVersion, List.of());
        }
        var v = new GardenLinuxVersion(gardenlinuxVersion);
        var cvesNewVersion = glvdService.getCveForDistribution(v.printVersion(), new SortAndPageOptions("cveId", "ASC", null, null));
        var cvesOldVersion = glvdService.getCveForDistribution(v.previousPatchVersion(), new SortAndPageOptions("cveId", "ASC", null, null));
        var cvesNewVersionCveIds = cvesNewVersion.stream().map(SourcePackageCve::getCveId).collect(Collectors.joining());
        var diff = cvesOldVersion.stream().filter(sourcePackageCve -> !cvesNewVersionCveIds.contains(sourcePackageCve.getCveId())).toList();
        var packagesNew = glvdService.sourcePackagesByGardenLinuxVersion(v.printVersion());
        var packagesOld = glvdService.sourcePackagesByGardenLinuxVersion(v.previousPatchVersion());
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
                                getVersionByPackageName(packagesOld, sourcePackage),
                                getVersionByPackageName(packagesNew, sourcePackage),
                                cves)
                )
        );

        return new ReleaseNote(gardenlinuxVersion, releaseNotesPackages);
    }

}
