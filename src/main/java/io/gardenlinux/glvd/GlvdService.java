package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.*;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlvdService {

    @Nonnull
    private final SourcePackageCveRepository sourcePackageCveRepository;

    @Nonnull
    private final SourcePackageRepository sourcePackageRepository;

    public GlvdService(@Nonnull SourcePackageCveRepository sourcePackageCveRepository, @Nonnull SourcePackageRepository sourcePackageRepository) {
        this.sourcePackageCveRepository = sourcePackageCveRepository;
        this.sourcePackageRepository = sourcePackageRepository;
    }

    public List<SourcePackageCve> getCveForDistribution(String gardenlinuxVersion) {
        return sourcePackageCveRepository.findByGardenlinuxVersion(gardenlinuxVersion);
    }

    public List<SourcePackageCve> getCveForPackages(String gardenlinuxVersion, String packages) {
        return sourcePackageCveRepository.findBySourcePackageNameInAndGardenlinuxVersion("{"+packages+"}", gardenlinuxVersion);
    }

    public List<SourcePackage> getPackagesForDistro(String gardenlinuxVersion) {
        return sourcePackageRepository.findByGardenlinuxVersion(gardenlinuxVersion);
    }

    public List<SourcePackageCve> getPackageWithVulnerabilities(String sourcePackage) {
        return sourcePackageCveRepository.findBySourcePackageName(sourcePackage);
    }

    public List<SourcePackageCve> getPackageWithVulnerabilitiesByVersion(String sourcePackage, String sourcePackageVersion) {
        return sourcePackageCveRepository.findBySourcePackageNameAndSourcePackageVersion(sourcePackage, sourcePackageVersion);
    }

    public List<SourcePackageCve> getPackagesByVulnerability(String gardenlinuxVersion, String cveId) {
        return sourcePackageCveRepository.findByCveIdAndGardenlinuxVersion(cveId, gardenlinuxVersion);
    }
}
