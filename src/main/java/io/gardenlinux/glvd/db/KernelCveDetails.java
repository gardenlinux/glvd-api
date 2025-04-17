package io.gardenlinux.glvd.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "kernel_cvedetails")
public class KernelCveDetails {
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

    @Column(name = "ingested", nullable = false)
    private String cveIngestedDate;

    @Column(name = "lts_version", nullable = false)
    private List<String> ltsVersion;

    @Column(name = "fixed_version", nullable = false)
    private List<String> fixedVersion;

    @Column(name = "is_fixed", nullable = true)
    private List<Boolean> isFixed;

    @Column(name = "is_relevant_subsystem", nullable = true)
    private List<Boolean> isRelevantSubsystem;

    @Column(name = "base_score_v40", nullable = true)
    private Float baseScoreV40;

    @Column(name = "base_score_v31", nullable = true)
    private Float baseScoreV31;

    @Column(name = "base_score_v30", nullable = true)
    private Float baseScoreV30;

    @Column(name = "base_score_v2", nullable = true)
    private Float baseScoreV2;

    @Column(name = "vector_string_v40", nullable = true)
    private String vectorStringV40;

    @Column(name = "vector_string_v31", nullable = true)
    private String vectorStringV31;

    @Column(name = "vector_string_v30", nullable = true)
    private String vectorStringV30;

    @Column(name = "vector_string_v2", nullable = true)
    private String vectorStringV2;

    public KernelCveDetails() {
    }

    public KernelCveDetails(String cveId, String vulnStatus, String description, String cvePublishedDate, String cveModifiedDate, String cveIngestedDate, List<String> ltsVersion, List<String> fixedVersion, List<Boolean> isFixed, List<Boolean> isRelevantSubsystem, Float baseScoreV40, Float baseScoreV31, Float baseScoreV30, Float baseScoreV2, String vectorStringV40, String vectorStringV31, String vectorStringV30, String vectorStringV2) {
        this.cveId = cveId;
        this.vulnStatus = vulnStatus;
        this.description = description;
        this.cvePublishedDate = cvePublishedDate;
        this.cveModifiedDate = cveModifiedDate;
        this.cveIngestedDate = cveIngestedDate;
        this.ltsVersion = ltsVersion;
        this.fixedVersion = fixedVersion;
        this.isFixed = isFixed;
        this.isRelevantSubsystem = isRelevantSubsystem;
        this.baseScoreV40 = baseScoreV40;
        this.baseScoreV31 = baseScoreV31;
        this.baseScoreV30 = baseScoreV30;
        this.baseScoreV2 = baseScoreV2;
        this.vectorStringV40 = vectorStringV40;
        this.vectorStringV31 = vectorStringV31;
        this.vectorStringV30 = vectorStringV30;
        this.vectorStringV2 = vectorStringV2;
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

    public String getCveIngestedDate() {
        return cveIngestedDate;
    }

    public List<String> getLtsVersion() {
        return ltsVersion;
    }

    public List<String> getFixedVersion() {
        return fixedVersion;
    }

    public List<Boolean> getIsFixed() {
        return isFixed;
    }

    public List<Boolean> getIsRelevantSubsystem() {
        return isRelevantSubsystem;
    }

    public Float getBaseScoreV40() {
        return baseScoreV40;
    }

    public Float getBaseScoreV31() {
        return baseScoreV31;
    }

    public Float getBaseScoreV30() {
        return baseScoreV30;
    }

    public Float getBaseScoreV2() {
        return baseScoreV2;
    }

    public String getVectorStringV40() {
        return vectorStringV40;
    }

    public String getVectorStringV31() {
        return vectorStringV31;
    }

    public String getVectorStringV30() {
        return vectorStringV30;
    }

    public String getVectorStringV2() {
        return vectorStringV2;
    }
}
