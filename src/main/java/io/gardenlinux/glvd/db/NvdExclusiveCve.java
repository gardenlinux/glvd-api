package io.gardenlinux.glvd.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nvd_exclusive_cve_matching_gl")
public class NvdExclusiveCve {
    @Id
    @Column(name = "cve_id", nullable = false)
    private String cveId;

    @Column(name = "vulnstatus", nullable = false)
    private String vulnStatus;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "published", nullable = false)
    private String cvePublishedDate;

    @Column(name = "modified", nullable = false)
    private String cveModifiedDate;

    public NvdExclusiveCve() {
    }

    public NvdExclusiveCve(String cveId, String vulnStatus, String description, String cvePublishedDate, String cveModifiedDate) {
        this.cveId = cveId;
        this.vulnStatus = vulnStatus;
        this.description = description;
        this.cvePublishedDate = cvePublishedDate;
        this.cveModifiedDate = cveModifiedDate;
    }

    public String getCveId() {
        return cveId;
    }

    public String getVulnStatus() {
        return vulnStatus;
    }

    public String getDescription() {
        return description;
    }

    public String getCvePublishedDate() {
        return cvePublishedDate;
    }

    public String getCveModifiedDate() {
        return cveModifiedDate;
    }
}
