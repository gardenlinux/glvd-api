package io.gardenlinux.glvd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenlinux.glvd.db.CveRepository;
import io.gardenlinux.glvd.db.HealthCheckRepository;
import io.gardenlinux.glvd.dto.Cve;
import io.gardenlinux.glvd.dto.Readiness;
import io.gardenlinux.glvd.exceptions.DbNotConnectedException;
import io.gardenlinux.glvd.exceptions.NotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public Cve getCve(String cveId) throws NotFoundException {
        var cveEntity = cveRepository.findById(cveId).orElseThrow(NotFoundException::new);
        // Todo: more specific transformation from db type 'cve' to response type 'cve'
        try {
            return new ObjectMapper().readValue(cveEntity.getData(), Cve.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Cve> getCveForDistribution(String vendor, String product, String codename) {
        var entities = cveRepository.cvesForDistribution(vendor, product, codename);
        var ret = List.of(entities.stream().map(cveEntity -> {
            try {
                return new ObjectMapper().readValue(cveEntity.getData(), Cve.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));

        return ret.get(0);
    }

}
