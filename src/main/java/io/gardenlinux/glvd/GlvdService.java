package io.gardenlinux.glvd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenlinux.glvd.db.CveEntity;
import io.gardenlinux.glvd.db.CveRepository;
import io.gardenlinux.glvd.db.HealthCheckRepository;
import io.gardenlinux.glvd.db.SourcePackageCve;
import io.gardenlinux.glvd.dto.Cve;
import io.gardenlinux.glvd.dto.Readiness;
import io.gardenlinux.glvd.exceptions.CantParseJSONException;
import io.gardenlinux.glvd.exceptions.DbNotConnectedException;
import io.gardenlinux.glvd.exceptions.NotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

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

    public Cve getCve(String cveId) throws NotFoundException, CantParseJSONException {
        var cveEntity = cveRepository.findById(cveId).orElseThrow(NotFoundException::new);

        return cveEntityDataToDomainEntity(cveEntity);
    }

    public List<Cve> getCveForDistribution(String product, String codename) throws CantParseJSONException {
        var entities = cveRepository.cvesForDistribution(product, codename);

        return entities.stream().map(this::cveEntityDataToDomainEntity).toList();
    }

    public List<Cve> getCveForDistributionVersion(String product, String version) throws CantParseJSONException {
        var entities = cveRepository.cvesForDistributionVersion(product, version);

        return entities.stream().map(this::cveEntityDataToDomainEntity).toList();
    }

    public List<SourcePackageCve> getCveForPackages(String product, String codename, String packages) {
        return cveRepository.cvesForPackageList(product, codename,"{"+packages+"}").stream().map(getSourcePackageCveFunction()).toList();
    }

    public List<SourcePackageCve> getCveForPackagesVersion(String product, String version, String packages) {
        return cveRepository.cvesForPackageListVersion(product, version,"{"+packages+"}").stream().map(getSourcePackageCveFunction()).toList();
    }

    private static Function<String, SourcePackageCve> getSourcePackageCveFunction() {
        return entity -> {
            var parts = entity.split(",");
            if (parts.length != 3) {
                throw new RuntimeException("Unexpected format");
            }
            var sourcePackage = parts[0];
            var cveId = parts[1];
            var publishedDate = parts[2];
            return new SourcePackageCve(cveId, publishedDate, sourcePackage);
        };
    }

    private Cve cveEntityDataToDomainEntity(CveEntity cveEntity) throws CantParseJSONException {
        try {
            return objectMapper.readValue(cveEntity.getData(), Cve.class);
        } catch (JsonProcessingException e) {
            throw new CantParseJSONException("Failed to parse JSON object into domain classes:\n====\n" + cveEntity.getData() + "\n====");
        }
    }
}
