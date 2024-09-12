package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.*;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Sort;
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

    public List<SourcePackageCve> getCveForDistribution(String gardenlinuxVersion, String sortBy, String sortOrder) {
        return sourcePackageCveRepository.findByGardenlinuxVersion(gardenlinuxVersion, Sort.by(Sort.Direction.valueOf(sortOrder), sortBy));
    }

    public List<SourcePackageCve> getCveForPackages(String gardenlinuxVersion, String packages) {
        return sourcePackageCveRepository.findBySourcePackageNameInAndGardenlinuxVersion("{"+packages+"}", gardenlinuxVersion);
    }

    public List<SourcePackage> getPackagesForDistro(String gardenlinuxVersion, String sortBy, String sortOrder) {
        return sourcePackageRepository.findByGardenlinuxVersion(gardenlinuxVersion, Sort.by(Sort.Direction.valueOf(sortOrder), sortBy));
    }

    public List<SourcePackageCve> getPackageWithVulnerabilities(String sourcePackage, String sortBy, String sortOrder) {
        return sourcePackageCveRepository.findBySourcePackageName(sourcePackage, Sort.by(Sort.Direction.valueOf(sortOrder), sortBy));
    }

    public List<SourcePackageCve> getPackageWithVulnerabilitiesByVersion(String sourcePackage, String sourcePackageVersion, String sortBy) {
        return sourcePackageCveRepository.findBySourcePackageNameAndSourcePackageVersion(sourcePackage, sourcePackageVersion, Sort.by(Sort.Direction.DESC, sortBy));
    }

    public List<SourcePackageCve> getPackagesByVulnerability(String gardenlinuxVersion, String cveId, String sortBy, String sortOrder) {
        return sourcePackageCveRepository.findByCveIdAndGardenlinuxVersion(cveId, gardenlinuxVersion, Sort.by(Sort.Direction.valueOf(sortOrder), sortBy));
    }

}
