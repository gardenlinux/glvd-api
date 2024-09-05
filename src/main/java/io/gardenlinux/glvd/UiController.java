package io.gardenlinux.glvd;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UiController {

    @Nonnull
    private final GlvdService glvdService;

    public UiController(@Nonnull GlvdService glvdService) {
        this.glvdService = glvdService;
    }

    @GetMapping("/getPackagesForDistro")
    public String getPackagesForDistro(
            @RequestParam(name = "distro", required = false, defaultValue = "gardenlinux") String distro,
            @RequestParam(name = "version", required = true) String version,
            Model model) {
        var packages = glvdService.getPackagesForDistro(distro, version);
        model.addAttribute("packages", packages);
        model.addAttribute("distro", distro);
        model.addAttribute("version", version);
        return "getPackagesForDistro";
    }

    @GetMapping("/getCveForDistribution")
    public String getCveForDistribution(
            @RequestParam(name = "distro", required = false, defaultValue = "gardenlinux") String distro,
            @RequestParam(name = "version", required = true) String version,
            Model model
    ) {
        var sourcePackageCves = glvdService.getCveForDistribution(distro, version);
        model.addAttribute("sourcePackageCves", sourcePackageCves);
        model.addAttribute("distro", distro);
        model.addAttribute("version", version);
        return "getCveForDistribution";
    }

    @GetMapping("/getCveForPackages")
    public String getCveForPackages(
            @RequestParam(name = "distro", required = false, defaultValue = "gardenlinux") String distro,
            @RequestParam(name = "version", required = true) String version,
            @RequestParam(name = "packages", required = true) String packages,
            Model model
    ) {
        var sourcePackageCves = glvdService.getCveForPackages(distro, version, packages);
        model.addAttribute("sourcePackageCves", sourcePackageCves);
        model.addAttribute("distro", distro);
        model.addAttribute("version", version);
        model.addAttribute("packages", packages);
        return "getCveForPackages";
    }

}