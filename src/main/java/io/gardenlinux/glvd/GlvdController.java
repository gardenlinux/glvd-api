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

    @GetMapping("/cves/{distro}/{distroVersion}")
    ResponseEntity<List<SourcePackageCve>> getCveDistro(@PathVariable final String distro,
                                                        @PathVariable final String distroVersion) {
        return ResponseEntity.ok().body(glvdService.getCveForDistribution(distro, distroVersion));
    }

    @GetMapping("/cves/{distro}/{distroVersion}/packages/{packageList}")
    ResponseEntity<List<SourcePackageCve>> getCvePackages(@PathVariable final String distro,
                                                          @PathVariable final String distroVersion, @PathVariable final String packageList) {
        var cveForPackages = glvdService.getCveForPackages(distro, distroVersion, packageList);
        return ResponseEntity.ok().body(cveForPackages);
    }

    @GetMapping("/packages/distro/{distro}/{distroVersion}")
    ResponseEntity<List<SourcePackage>> packagesForDistro(@PathVariable final String distro, @PathVariable final String distroVersion) {
        return ResponseEntity.ok(glvdService.getPackagesForDistro(distro, distroVersion));
    }

    @GetMapping("/packages/{sourcePackage}")
    ResponseEntity<List<SourcePackageCve>> packageWithVulnerabilities(@PathVariable final String sourcePackage) {
        return ResponseEntity.ok(glvdService.getPackageWithVulnerabilities(sourcePackage));
    }

    @GetMapping("/packages/{sourcePackage}/{sourcePackageVersion}")
    ResponseEntity<List<SourcePackageCve>> packageWithVulnerabilitiesByVersion(@PathVariable final String sourcePackage, @PathVariable final String sourcePackageVersion) {
        return ResponseEntity.ok(glvdService.getPackageWithVulnerabilitiesByVersion(sourcePackage, sourcePackageVersion));
    }

    @GetMapping("/packages/distro/{distro}/{distroVersion}/{cveId}")
    ResponseEntity<List<SourcePackageCve>> packagesByVulnerability(@PathVariable final String distro, @PathVariable final String distroVersion, @PathVariable final String cveId) {
        return ResponseEntity.ok(glvdService.getPackagesByVulnerability(distro, distroVersion, cveId));
    }

}
