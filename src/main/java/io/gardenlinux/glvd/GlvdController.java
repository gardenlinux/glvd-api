package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.CveEntity;
import io.gardenlinux.glvd.db.SourcePackageCve;
import io.gardenlinux.glvd.exceptions.NotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/cves", produces = MediaType.APPLICATION_JSON_VALUE)
public class GlvdController {

    @Nonnull
    private final GlvdService glvdService;

    public GlvdController(@Nonnull GlvdService glvdService) {
        this.glvdService = glvdService;
    }

    @GetMapping("/{cveId}")
    ResponseEntity<CveEntity> getCveId(@PathVariable("cveId") final String cveId) throws NotFoundException {
        return ResponseEntity.ok().body(glvdService.getCve(cveId));
    }

    @GetMapping("/{product}/{codename}")
    ResponseEntity<List<SourcePackageCve>> getCveDistro(@PathVariable final String product,
                                              @PathVariable final String codename) {
        return ResponseEntity.ok().body(glvdService.getCveForDistribution(product, codename));
    }


    @GetMapping("/{product}/version/{version}")
    ResponseEntity<List<SourcePackageCve>> getCveDistroVersion(@PathVariable final String product,
                                           @PathVariable final String version) {
        return ResponseEntity.ok().body(glvdService.getCveForDistributionVersion(product, version));
    }


    @GetMapping("/{product}/{codename}/packages/{packageList}")
    ResponseEntity<List<SourcePackageCve>> getCvePackages(@PathVariable final String product,
                                                          @PathVariable final String codename, @PathVariable final String packageList) {
        var cveForPackages = glvdService.getCveForPackages(product, codename, packageList);
        return ResponseEntity.ok().body(cveForPackages);
    }

    @GetMapping("/{product}/version/{version}/packages/{packageList}")
    ResponseEntity<List<SourcePackageCve>> getCvePackagesVersion(@PathVariable final String product,
                                                          @PathVariable final String version, @PathVariable final String packageList) {
        var cveForPackages = glvdService.getCveForPackagesVersion(product, version, packageList);
        return ResponseEntity.ok().body(cveForPackages);
    }

}
