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
            @RequestParam(name = "gardenlinuxVersion", required = true) String gardenlinuxVersion,
            Model model) {
        var packages = glvdService.getPackagesForDistro(gardenlinuxVersion);
        model.addAttribute("packages", packages);
        model.addAttribute("gardenlinuxVersion", gardenlinuxVersion);
        return "getPackagesForDistro";
    }

    @GetMapping("/getCveForDistribution")
    public String getCveForDistribution(
            @RequestParam(name = "gardenlinuxVersion", required = true) String gardenlinuxVersion,
            Model model
    ) {
        var sourcePackageCves = glvdService.getCveForDistribution(gardenlinuxVersion);
        model.addAttribute("sourcePackageCves", sourcePackageCves);
        model.addAttribute("gardenlinuxVersion", gardenlinuxVersion);
        return "getCveForDistribution";
    }

    @GetMapping("/getCveForPackages")
    public String getCveForPackages(

            @RequestParam(name = "gardenlinuxVersion", required = true) String gardenlinuxVersion,
            @RequestParam(name = "packages", required = true) String packages,
            Model model
    ) {
        var sourcePackageCves = glvdService.getCveForPackages(gardenlinuxVersion, packages);
        model.addAttribute("sourcePackageCves", sourcePackageCves);
        model.addAttribute("gardenlinuxVersion", gardenlinuxVersion);
        model.addAttribute("packages", packages);
        return "getCveForPackages";
    }

    @GetMapping("/getPackagesByVulnerability")
    public String getPackagesByVulnerability(

            @RequestParam(name = "gardenlinuxVersion", required = true) String gardenlinuxVersion,
            @RequestParam(name = "cveId", required = true) String cveId,
            Model model
    ) {
        var sourcePackageCves = glvdService.getPackagesByVulnerability(gardenlinuxVersion, cveId);
        model.addAttribute("sourcePackageCves", sourcePackageCves);
        model.addAttribute("gardenlinuxVersion", gardenlinuxVersion);
        model.addAttribute("cveId", cveId);
        return "getPackagesByVulnerability";
    }

}