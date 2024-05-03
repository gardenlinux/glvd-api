package io.gardenlinux.glvd;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public class GlvdService {

    @Nonnull
    private final CveRepository cveRepository;

    public GlvdService(@Nonnull CveRepository cveRepository) {
        this.cveRepository = cveRepository;
    }

    public Cve getCve(String cveId) {
        var cve = cveRepository.findById(cveId);
        return cve.orElseThrow();
    }
}
