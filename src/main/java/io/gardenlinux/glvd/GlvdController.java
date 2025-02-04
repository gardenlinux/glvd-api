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

    // https://github.com/gardenlinux/glvd/issues/132
    // Assumptions:
    //  - Not used for .0 versions
    //  - No burnt versions
    @GetMapping("/patchReleaseNotes/{gardenlinuxVersion}")
    List<ReleaseNotesPackage> releaseNotes(@PathVariable final String gardenlinuxVersion) {
        var cvesNewVersion = glvdService.getCveForDistribution(gardenlinuxVersion, new SortAndPageOptions("cveId", "ASC", "0", "100000"));

        System.out.println(cvesNewVersion);

        var versionComponents = gardenlinuxVersion.split("\\.");
        var major = versionComponents[0];
        var patch = versionComponents[1];

        var patchAsInt = Integer.parseInt(patch);

        var oldVersion = major + "." + (patchAsInt - 1);

        var cvesOldVersion = glvdService.getCveForDistribution(oldVersion, new SortAndPageOptions("cveId", "ASC", "0", "100000"));

        System.out.println(cvesOldVersion);

        var cvesNewVersionCveIds = cvesNewVersion.stream().map(SourcePackageCve::getCveId).collect(Collectors.joining());

        var diff = cvesOldVersion.stream().filter(sourcePackageCve -> ! cvesNewVersionCveIds.contains(sourcePackageCve.getCveId())).toList();

        var packagesNew = glvdService.sourcePackagesByGardenLinuxVersion(gardenlinuxVersion);
        var packagesOld = glvdService.sourcePackagesByGardenLinuxVersion(oldVersion);


        HashMap<String, List<String>> ret = new HashMap<>();

        System.out.println(diff);

        for (SourcePackageCve sourcePackageCve : diff) {
            System.out.println(sourcePackageCve.getCveId());
            System.out.println(sourcePackageCve.getSourcePackageName());
//            System.out.println(sourcePackageCve.getSourcePackageVersion());

            for (DebSrc debSrc : packagesOld) {
                if (debSrc.getDebSource().equalsIgnoreCase(sourcePackageCve.getSourcePackageName())) {
                    System.out.println(debSrc.getDebVersion());
                }
            }

            for (DebSrc debSrc : packagesNew) {
                if (debSrc.getDebSource().equalsIgnoreCase(sourcePackageCve.getSourcePackageName())) {
                    System.out.println(debSrc.getDebVersion());
                }
            }

            var x = ret.get(sourcePackageCve.getSourcePackageName());
            if (x == null) {
                x = new ArrayList<>();
            }
            x.add(sourcePackageCve.getCveId());
            ret.put(sourcePackageCve.getSourcePackageName(), x);
        }

        List<ReleaseNotesPackage> xx = new ArrayList<>();
        ret.forEach((sourcePackage, cves) -> xx.add(new ReleaseNotesPackage(sourcePackage, "", "", cves)));

        return xx;
    }

}
