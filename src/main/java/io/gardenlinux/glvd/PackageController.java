package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.PackageEntity;
import jakarta.annotation.Nonnull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/packages", produces = MediaType.APPLICATION_JSON_VALUE)
public class PackageController {

    @Nonnull
    private final GlvdService glvdService;

    public PackageController(@Nonnull GlvdService glvdService) {
        this.glvdService = glvdService;
    }

    @GetMapping("/distro/{distro}/{distroVersion}")
    ResponseEntity<List<String>> packagesForDistro(@PathVariable final String distro, @PathVariable final String distroVersion) {
        return ResponseEntity.ok(glvdService.getPackagesForDistro(distro, distroVersion));
    }

    @GetMapping("/{sourcePackage}")
    ResponseEntity<List<PackageEntity>> packageWithVulnerabilities(@PathVariable final String sourcePackage) {
        return ResponseEntity.ok(glvdService.getPackageWithVulnerabilities(sourcePackage));
    }

    @GetMapping("/{sourcePackage}/{sourcePackageVersion}")
    ResponseEntity<List<PackageEntity>> packageWithVulnerabilitiesByVersion(@PathVariable final String sourcePackage, @PathVariable final String sourcePackageVersion) {
        return ResponseEntity.ok(glvdService.getPackageWithVulnerabilitiesByVersion(sourcePackage, sourcePackageVersion));
    }

    @GetMapping("/distro/{distro}/{distroVersion}/{cveId}")
    ResponseEntity<List<PackageEntity>> packagesByVulnerability(@PathVariable final String distro, @PathVariable final String distroVersion, @PathVariable final String cveId) {
        return ResponseEntity.ok(glvdService.getPackagesByVulnerability(distro, distroVersion, cveId));
    }
}
