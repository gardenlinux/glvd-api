package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.SourcePackage;
import io.gardenlinux.glvd.db.SourcePackageCve;
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
        return ResponseEntity.ok().body(glvdService.getCveForDistribution(gardenlinuxVersion, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)));
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
        var cveForPackages = glvdService.getCveForPackages(gardenlinuxVersion, packageList, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize));
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
        var cveForPackages = glvdService.getCveForPackages(gardenlinuxVersion, packageList, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize));
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
        return ResponseEntity.ok(glvdService.getPackageWithVulnerabilities(sourcePackage, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)));
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
        return ResponseEntity.ok(glvdService.getPackageWithVulnerabilitiesByVersion(sourcePackage, sourcePackageVersion, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)));
    }

    @GetMapping("/distro/{gardenlinuxVersion}")
    ResponseEntity<List<SourcePackage>> packagesForDistro(
            @PathVariable final String gardenlinuxVersion,
            @RequestParam(defaultValue = "sourcePackageName") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize
    ) {
        return ResponseEntity.ok(glvdService.getPackagesForDistro(gardenlinuxVersion, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)));
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
        return ResponseEntity.ok(glvdService.getPackagesByVulnerability(gardenlinuxVersion, cveId, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)));
    }

}
