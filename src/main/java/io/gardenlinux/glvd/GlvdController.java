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
            @RequestParam(defaultValue = "ASC") final String sortOrder
    ) {
        return ResponseEntity.ok().body(glvdService.getCveForDistribution(gardenlinuxVersion, sortBy, sortOrder));
    }

    @GetMapping("/cves/{gardenlinuxVersion}/packages/{packageList}")
    ResponseEntity<List<SourcePackageCve>> getCvePackages(
            @PathVariable final String gardenlinuxVersion, @PathVariable final String packageList) {
        var cveForPackages = glvdService.getCveForPackages(gardenlinuxVersion, packageList);
        return ResponseEntity.ok().body(cveForPackages);
    }

    @GetMapping("/packages/{sourcePackage}")
    ResponseEntity<List<SourcePackageCve>> packageWithVulnerabilities(
            @PathVariable final String sourcePackage,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder
    ) {
        return ResponseEntity.ok(glvdService.getPackageWithVulnerabilities(sourcePackage, sortBy, sortOrder));
    }

    @GetMapping("/packages/{sourcePackage}/{sourcePackageVersion}")
    ResponseEntity<List<SourcePackageCve>> packageWithVulnerabilitiesByVersion(
            @PathVariable final String sourcePackage,
            @PathVariable final String sourcePackageVersion,
            @RequestParam(defaultValue = "cveId") final String sortBy
    ) {
        return ResponseEntity.ok(glvdService.getPackageWithVulnerabilitiesByVersion(sourcePackage, sourcePackageVersion, sortBy));
    }

    @GetMapping("/distro/{gardenlinuxVersion}")
    ResponseEntity<List<SourcePackage>> packagesForDistro(
            @PathVariable final String gardenlinuxVersion,
            @RequestParam(defaultValue = "sourcePackageName") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder
    ) {
        return ResponseEntity.ok(glvdService.getPackagesForDistro(gardenlinuxVersion, sortBy, sortOrder));
    }

    @GetMapping("/distro/{gardenlinuxVersion}/{cveId}")
    ResponseEntity<List<SourcePackageCve>> packagesByVulnerability(
            @PathVariable final String gardenlinuxVersion,
            @PathVariable final String cveId,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder
    ) {
        return ResponseEntity.ok(glvdService.getPackagesByVulnerability(gardenlinuxVersion, cveId, sortBy, sortOrder));
    }

}
