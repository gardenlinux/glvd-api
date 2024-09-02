package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.CveEntity;
import io.gardenlinux.glvd.db.CveRepository;
import io.gardenlinux.glvd.db.HealthCheckRepository;
import io.gardenlinux.glvd.db.SourcePackageCve;
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
    private final HealthCheckRepository healthCheckRepository;

    public GlvdService(@Nonnull CveRepository cveRepository, @Nonnull HealthCheckRepository healthCheckRepository) {
        this.cveRepository = cveRepository;
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

    public List<String> getPackagesForDistro(String glVersion) {
        return cveRepository.packagesForDistribution(glVersion);
    }
}
