package io.gardenlinux.glvd.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "kernel_cve")
public class KernelCve {

    @Id
    @Column(name = "cve_id", nullable = false)
    private String cveId;

    @Column(name = "source_package_name", nullable = false)
    private String sourcePackageName;

    @Column(name = "source_package_version", nullable = false)
    private String sourcePackageVersion;

    @Column(name = "gardenlinux_version", nullable = false)
    private String gardenlinuxVersion;

    @Column(name = "is_vulnerable", nullable = false)
    private boolean isVulnerable;

    @Column(name = "fixed_version", nullable = true)
    private String fixedVersion;

    @Column(name = "cve_published_date", nullable = false)
    private String cvePublishedDate;

    @Column(name = "cve_last_modified_date", nullable = false)
    private String cveLastModifiedDate;

    @Column(name = "cve_last_ingested_date", nullable = false)
    private String cveLastIngestedDate;

    @Column(name = "base_score", nullable = true)
    private Float baseScore;

    @Column(name = "vector_string", nullable = true)
    private String vectorString;

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

    public KernelCve(String cveId, String sourcePackageName, String sourcePackageVersion, String gardenlinuxVersion, boolean isVulnerable, String fixedVersion, String cvePublishedDate, String cveLastModifiedDate, String cveLastIngestedDate, Float baseScore, String vectorString, Float baseScoreV40, Float baseScoreV31, Float baseScoreV30, Float baseScoreV2, String vectorStringV40, String vectorStringV31, String vectorStringV30, String vectorStringV2) {
        this.cveId = cveId;
        this.sourcePackageName = sourcePackageName;
        this.sourcePackageVersion = sourcePackageVersion;
        this.gardenlinuxVersion = gardenlinuxVersion;
        this.isVulnerable = isVulnerable;
        this.fixedVersion = fixedVersion;
        this.cvePublishedDate = cvePublishedDate;
        this.cveLastModifiedDate = cveLastModifiedDate;
        this.cveLastIngestedDate = cveLastIngestedDate;
        this.baseScore = baseScore;
        this.vectorString = vectorString;
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

    public String getSourcePackageName() {
        return sourcePackageName;
    }

    public String getSourcePackageVersion() {
        return sourcePackageVersion;
    }

    public String getGardenlinuxVersion() {
        return gardenlinuxVersion;
    }

    public boolean isVulnerable() {
        return isVulnerable;
    }

    public String getFixedVersion() {
        return fixedVersion;
    }

    public String getCvePublishedDate() {
        return cvePublishedDate;
    }

    public String getCveLastModifiedDate() {
        return cveLastModifiedDate;
    }

    public String getCveLastIngestedDate() {
        return cveLastIngestedDate;
    }

    public Float getBaseScore() {
        return baseScore;
    }

    public String getVectorString() {
        return vectorString;
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
