package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.*;
import io.gardenlinux.glvd.exceptions.CveNotKnownException;
import io.gardenlinux.glvd.exceptions.InvalidGardenLinuxVersionException;
import io.gardenlinux.glvd.releasenotes.ReleaseNote;
import jakarta.annotation.Nonnull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    ResponseEntity<List<SourcePackageCve>> getCvePackagesPut(
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

    // Internal API, undocumented on purpose
    // This is needed for the triage implementation
    // No external consumers should use this endpoint
    @GetMapping(value = "/distro/{gardenlinuxVersion}/distId", produces = "text/plain")
    ResponseEntity<String> distIdForDistro(
            @PathVariable final String gardenlinuxVersion
    ) {
        return ResponseEntity.ok(
                glvdService.distVersionToId(gardenlinuxVersion)
        );
    }

    @GetMapping("/gardenlinuxVersions")
    ResponseEntity<List<String>> getAllDistros() {
        return ResponseEntity.ok(glvdService.allGardenLinuxVersions());
    }

    @GetMapping("/cveDetails/{cveId}")
    ResponseEntity<CveDetailsWithContext> cveDetails(@PathVariable final String cveId) {
        CveDetail cveDetails = null;
        try {
            cveDetails = glvdService.getCveDetails(cveId);
        } catch (CveNotKnownException e) {
            return ResponseEntity.notFound().header("Message", e.getMessage()).build();
        }
        var cveContexts = glvdService.getCveContextsForCveId(cveId);

        return ResponseEntity.ok(new CveDetailsWithContext(cveDetails, cveContexts));
    }

    // https://github.com/gardenlinux/glvd/issues/132
    // Assumptions:
    //  - Version numbers follow old major.patch versioning schema,
    //      for new schema, use releaseNotes
    //  - Not used for .0 versions
    //  - No burnt versions
    @GetMapping("/patchReleaseNotes/{gardenlinuxVersion}")
    @Deprecated(since = "2025-09-29")
    ReleaseNote releaseNotesTwoDigit(@PathVariable final String gardenlinuxVersion) {
        return glvdService.releaseNoteTwoDigitVersion(gardenlinuxVersion);
    }

    @GetMapping("/releaseNotes/{gardenlinuxVersion}")
    ResponseEntity<ReleaseNote> releaseNotes(@PathVariable final String gardenlinuxVersion) {
        try {
            return ResponseEntity.ok(glvdService.releaseNote(gardenlinuxVersion));
        } catch (InvalidGardenLinuxVersionException invalidGardenLinuxVersionException) {
            return ResponseEntity.badRequest().header("Message", invalidGardenLinuxVersionException.getMessage()).build();
        }
    }

    @GetMapping("/kernel/gardenlinux/{gardenlinuxVersion}")
    ResponseEntity<List<KernelCve>> kernelCvesGardenlinux(@PathVariable String gardenlinuxVersion) {
        return ResponseEntity.ok(glvdService.kernelCvesForGardenLinuxVersion(gardenlinuxVersion));
    }

    @GetMapping("/triage/{gardenlinuxVersion}")
    ResponseEntity<List<CveContext>> triageDataGardenlinux(@PathVariable String gardenlinuxVersion) {
        return ResponseEntity.ok(glvdService.getCveContextsForGardenLinuxVersion(gardenlinuxVersion));
    }

    @GetMapping("/triage")
    ResponseEntity<List<CveContext>> triageDataRecent() {
        return ResponseEntity.ok(glvdService.getCveContexts());
    }
}
