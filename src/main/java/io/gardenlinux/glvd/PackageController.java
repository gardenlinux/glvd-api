package io.gardenlinux.glvd;

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

    @GetMapping("/{glVersion}")
    ResponseEntity<List<String>> foo(@PathVariable final String glVersion) {
        return ResponseEntity.ok(glvdService.getPackagesForDistro(glVersion));
    }
}
