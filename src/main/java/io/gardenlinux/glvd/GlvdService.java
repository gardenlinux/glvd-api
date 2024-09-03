package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.*;
import io.gardenlinux.glvd.dto.Readiness;
import io.gardenlinux.glvd.exceptions.DbNotConnectedException;
import io.gardenlinux.glvd.exceptions.NotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlvdService {

    @Nonnull
    private final CveRepository cveRepository;

    @Nonnull
    private final PackagesRepository packagesRepository;

    @Nonnull
    private final HealthCheckRepository healthCheckRepository;

    public GlvdService(@Nonnull CveRepository cveRepository, @Nonnull PackagesRepository packagesRepository, @Nonnull HealthCheckRepository healthCheckRepository) {
        this.cveRepository = cveRepository;
        this.packagesRepository = packagesRepository;
        this.healthCheckRepository = healthCheckRepository;
    }

    public Readiness getReadiness() throws DbNotConnectedException {
        try {
            var connection = healthCheckRepository.checkDbConnection();
            return new Readiness(connection);
        } catch (Exception e) {
            throw new DbNotConnectedException(e);
        }
    }

    // Not the most elegant solution. This might be replaced by a VIEW in the database,
    // or some other feature in spring data jpa?
    private SourcePackageCve parseDbResponse(String input) {
        var parts = input.split(",");
        var packageName = parts[0];
        var cveId = parts[1];
        var cvePublishedDate = parts[2];
        return new SourcePackageCve(cveId, cvePublishedDate, packageName);
    }

    public CveEntity getCve(String cveId) throws NotFoundException {
        return cveRepository.findById(cveId).orElseThrow(NotFoundException::new);
    }

    public List<SourcePackageCve> getCveForDistribution(String product, String codename) {
        return cveRepository.cvesForDistribution(product, codename).stream().map(this::parseDbResponse).toList();
    }

    public List<SourcePackageCve> getCveForDistributionVersion(String product, String version) {
        return cveRepository.cvesForDistributionVersion(product, version).stream().map(this::parseDbResponse).toList();
    }

    public List<SourcePackageCve> getCveForPackages(String product, String codename, String packages) {
        return cveRepository.cvesForPackageList(product, codename,"{"+packages+"}").stream().map(this::parseDbResponse).toList();
    }

    public List<SourcePackageCve> getCveForPackagesVersion(String product, String version, String packages) {
        return cveRepository.cvesForPackageListVersion(product, version,"{"+packages+"}").stream().map(this::parseDbResponse).toList();
    }

    public List<String> getPackagesForDistro(String distro, String distroVersion) {
        return cveRepository.packagesForDistribution(distro, distroVersion);
    }

    private PackageEntity parseDbResponsePackageWithVulnerabilities(String input) {
        var parts = input.split(",");
        var cveId = parts[0];
        var debSource = parts[1];
        var debVersion = parts[2];
        var debsecVulnerable = parts[3];
        return new PackageEntity(cveId, debSource, debVersion, debsecVulnerable);
    }

    public List<PackageEntity> getPackageWithVulnerabilities(String sourcePackage) {
        return packagesRepository.packageWithVulnerabilities(sourcePackage);
    }

    public List<PackageEntity> getPackageWithVulnerabilitiesByVersion(String sourcePackage, String sourcePackageVersion) {
        return packagesRepository.packageWithVulnerabilitiesByVersion(sourcePackage, sourcePackageVersion);
    }
}
