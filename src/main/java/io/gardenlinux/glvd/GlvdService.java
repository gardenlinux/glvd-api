package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.CveRepository;
import io.gardenlinux.glvd.dto.Cve;
import io.gardenlinux.glvd.exceptions.NotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlvdService {

    @Nonnull
    private final CveRepository cveRepository;

    public GlvdService(@Nonnull CveRepository cveRepository) {
        this.cveRepository = cveRepository;
    }

    public Cve getCve(String cveId) throws NotFoundException {
        var cveEntity = cveRepository.findById(cveId).orElseThrow(NotFoundException::new);
        // Todo: more specific transformation from db type 'cve' to response type 'cve'
        return new Cve(cveEntity.getId(), cveEntity.getLastModified(), cveEntity.getData());

    }

    public List<String> getCveForDistribution(String vendor, String product, String codename) {
        return cveRepository.cvesForDistribution(vendor, product, codename);
    }
}
