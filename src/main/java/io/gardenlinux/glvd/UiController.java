package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.SourcePackageCve;
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
            @RequestParam(defaultValue = "sourcePackageName") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize,
            Model model) {
        var packages = glvdService.getPackagesForDistro(
                gardenlinuxVersion, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)
        );
        model.addAttribute("packages", packages);
        model.addAttribute("gardenlinuxVersion", gardenlinuxVersion);
        return "getPackagesForDistro";
    }

    @GetMapping("/getCveForDistribution")
    public String getCveForDistribution(
            @RequestParam(name = "gardenlinuxVersion", required = true) String gardenlinuxVersion,
            @RequestParam(defaultValue = "baseScore") final String sortBy,
            @RequestParam(defaultValue = "DESC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize,
            @RequestParam(required = false, defaultValue = "true") final boolean onlyVulnerable,
            Model model
    ) {
        var sourcePackageCves = glvdService.getCveForDistribution(
                gardenlinuxVersion, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)
        ).stream().filter(SourcePackageCve::isVulnerable).toList();
        var contexts = glvdService.getCveContextsForDist(glvdService.distVersionToId(gardenlinuxVersion));
        model.addAttribute("sourcePackageCves", sourcePackageCves);
        model.addAttribute("gardenlinuxVersion", gardenlinuxVersion);
        model.addAttribute("onlyVulnerable", onlyVulnerable);
        model.addAttribute("cveContexts", contexts);
        return "getCveForDistribution";
    }

    @GetMapping("/getCveForDistributionAll")
    public String getCveForDistributionAll(
            @RequestParam(name = "gardenlinuxVersion", required = true) String gardenlinuxVersion,
            @RequestParam(defaultValue = "baseScore") final String sortBy,
            @RequestParam(defaultValue = "DESC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize,
            @RequestParam(required = false, defaultValue = "true") final boolean onlyVulnerable,
            Model model
    ) {
        var sourcePackageCves = glvdService.getCveForDistribution(
                gardenlinuxVersion, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)
        );
        var contexts = glvdService.getCveContextsForDist(glvdService.distVersionToId(gardenlinuxVersion));
        model.addAttribute("sourcePackageCves", sourcePackageCves);
        model.addAttribute("gardenlinuxVersion", gardenlinuxVersion);
        model.addAttribute("onlyVulnerable", onlyVulnerable);
        model.addAttribute("cveContexts", contexts);
        return "getCveForDistributionAll";
    }

    @GetMapping("/getCveForPackages")
    public String getCveForPackages(
            @RequestParam(name = "gardenlinuxVersion", required = true) String gardenlinuxVersion,
            @RequestParam(name = "packages", required = true) String packages,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize,
            Model model
    ) {
        var sourcePackageCves = glvdService.getCveForPackages(
                gardenlinuxVersion, packages, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)
        );
        model.addAttribute("sourcePackageCves", sourcePackageCves);
        model.addAttribute("gardenlinuxVersion", gardenlinuxVersion);
        model.addAttribute("packages", packages);
        return "getCveForPackages";
    }

    @GetMapping("/getPackagesByVulnerability")
    public String getPackagesByVulnerability(
            @RequestParam(name = "gardenlinuxVersion", required = true) String gardenlinuxVersion,
            @RequestParam(name = "cveId", required = true) String cveId,
            @RequestParam(defaultValue = "cveId") final String sortBy,
            @RequestParam(defaultValue = "ASC") final String sortOrder,
            @RequestParam(required = false) final String pageNumber,
            @RequestParam(required = false) final String pageSize,
            Model model
    ) {
        var sourcePackageCves = glvdService.getPackagesByVulnerability(
                gardenlinuxVersion, cveId, new SortAndPageOptions(sortBy, sortOrder, pageNumber, pageSize)
        );
        model.addAttribute("sourcePackageCves", sourcePackageCves);
        model.addAttribute("gardenlinuxVersion", gardenlinuxVersion);
        model.addAttribute("cveId", cveId);
        return "getPackagesByVulnerability";
    }

    @GetMapping("/getCveDetails")
    public String getCveDetails(@RequestParam(name = "cveId", required = true) String cveId, Model model) {
        var cveDetails = glvdService.getCveDetails(cveId);
        var cveContexts = glvdService.getCveContexts(cveId);
        model.addAttribute("cveDetails", cveDetails);
        model.addAttribute("cveContexts", cveContexts);
        return "getCveDetails";
    }

}