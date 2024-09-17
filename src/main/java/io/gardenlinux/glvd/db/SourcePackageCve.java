package io.gardenlinux.glvd.db;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "sourcepackagecve")
public class SourcePackageCve {

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

    @Column(name = "cve_published_date", nullable = false)
    private String cvePublishedDate;

    @Column(name = "base_score_v40", nullable = true)
    private Float baseScoreV40;

    @Column(name = "base_score_v31", nullable = true)
    private Float baseScoreV31;

    @Column(name = "base_score_v30", nullable = true)
    private Float baseScoreV30;

    @Column(name = "vector_string_v40", nullable = true)
    private String vectorStringV40;

    @Column(name = "vector_string_v31", nullable = true)
    private String vectorStringV31;

    @Column(name = "vector_string_v30", nullable = true)
    private String vectorStringV30;

    public SourcePackageCve() {
    }

    public SourcePackageCve(String cveId, String sourcePackageName, String sourcePackageVersion, String gardenlinuxVersion, boolean isVulnerable, String cvePublishedDate, Float baseScoreV40, Float baseScoreV31, Float baseScoreV30, String vectorStringV40, String vectorStringV31, String vectorStringV30) {
        this.cveId = cveId;
        this.sourcePackageName = sourcePackageName;
        this.sourcePackageVersion = sourcePackageVersion;
        this.gardenlinuxVersion = gardenlinuxVersion;
        this.isVulnerable = isVulnerable;
        this.cvePublishedDate = cvePublishedDate;
        this.baseScoreV40 = baseScoreV40;
        this.baseScoreV31 = baseScoreV31;
        this.baseScoreV30 = baseScoreV30;
        this.vectorStringV40 = vectorStringV40;
        this.vectorStringV31 = vectorStringV31;
        this.vectorStringV30 = vectorStringV30;
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

    public String getCvePublishedDate() {
        return cvePublishedDate;
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

    public String getVectorStringV40() {
        return vectorStringV40;
    }

    public String getVectorStringV31() {
        return vectorStringV31;
    }

    public String getVectorStringV30() {
        return vectorStringV30;
    }
}