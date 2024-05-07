package io.gardenlinux.glvd;

import io.gardenlinux.glvd.dto.Readiness;
import io.gardenlinux.glvd.exceptions.DbNotConnectedException;
import jakarta.annotation.Nonnull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @Nonnull
    private final GlvdService glvdService;

    public AppController(@Nonnull GlvdService glvdService) {
        this.glvdService = glvdService;
    }

    @GetMapping("/readiness")
    public Readiness readiness() throws DbNotConnectedException {
        return glvdService.getReadiness();
    }

}
