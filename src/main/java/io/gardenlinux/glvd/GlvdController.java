package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.SourcePackageCve;
import io.gardenlinux.glvd.dto.Cve;
import io.gardenlinux.glvd.exceptions.CantParseJSONException;
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
    ResponseEntity<Cve> getCveId(@PathVariable("cveId") final String cveId) throws NotFoundException {
        return ResponseEntity.ok().body(glvdService.getCve(cveId));
    }

    @GetMapping("/{vendor}/{product}/{codename}")
    ResponseEntity<List<Cve>> getCveDistro(@PathVariable final String vendor, @PathVariable final String product,
                                              @PathVariable final String codename) throws CantParseJSONException {
        return ResponseEntity.ok().body(glvdService.getCveForDistribution(vendor, product, codename));
    }

    @GetMapping("/{vendor}/{product}/{codename}/packages/{packageList}")
    ResponseEntity<List<SourcePackageCve>> getCvePackages(@PathVariable final String vendor, @PathVariable final String product,
                                                          @PathVariable final String codename, @PathVariable final String packageList) {
        var cveForPackages = glvdService.getCveForPackages(vendor, product, codename, packageList);
        return ResponseEntity.ok().body(cveForPackages);
    }

}
