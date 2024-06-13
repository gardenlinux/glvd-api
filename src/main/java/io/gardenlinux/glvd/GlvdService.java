package io.gardenlinux.glvd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenlinux.glvd.db.CveRepository;
import io.gardenlinux.glvd.db.HealthCheckRepository;
import io.gardenlinux.glvd.db.SourcePackageCve;
import io.gardenlinux.glvd.dto.Readiness;
import io.gardenlinux.glvd.exceptions.CantParseJSONException;
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

    private final ObjectMapper objectMapper;

    public GlvdService(@Nonnull CveRepository cveRepository, @Nonnull HealthCheckRepository healthCheckRepository) {
        this.cveRepository = cveRepository;
        this.healthCheckRepository = healthCheckRepository;
        this.objectMapper = new ObjectMapper();
    }

    public Readiness getReadiness() throws DbNotConnectedException {
        try {
            var connection = healthCheckRepository.checkDbConnection();
            return new Readiness(connection);
        } catch (Exception e) {
            throw new DbNotConnectedException(e);
        }
    }

    public SourcePackageCve getCve(String cveId) throws NotFoundException {
        return cveRepository.findById(cveId).orElseThrow(NotFoundException::new);
    }

    public List<SourcePackageCve> getCveForDistribution(String product, String codename) throws CantParseJSONException {
        return cveRepository.cvesForDistribution(product, codename);
    }

    public List<SourcePackageCve> getCveForDistributionVersion(String product, String version) throws CantParseJSONException {
        return cveRepository.cvesForDistributionVersion(product, version);
    }

    public List<SourcePackageCve> getCveForPackages(String product, String codename, String packages) {
        return cveRepository.cvesForPackageList(product, codename,"{"+packages+"}");
    }

    public List<SourcePackageCve> getCveForPackagesVersion(String product, String version, String packages) {
        return cveRepository.cvesForPackageListVersion(product, version,"{"+packages+"}");
    }
}
