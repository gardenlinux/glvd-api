package io.gardenlinux.glvd.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nvd_exclusive_cve")
public class NvdExclusiveCve {
    @Id
    @Column(name = "cve_id", nullable = false)
    private String cveId;

    public NvdExclusiveCve() {
    }

    public NvdExclusiveCve(String cveId) {
        this.cveId = cveId;
    }

    public String getCveId() {
        return cveId;
    }
}
