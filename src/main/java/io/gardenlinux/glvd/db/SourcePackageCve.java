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

    @Column(name = "base_score", nullable = true)
    private float baseScore;

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

    public SourcePackageCve() {
    }

    public SourcePackageCve(String cveId, float baseScore, String sourcePackageName, String sourcePackageVersion, String gardenlinuxVersion, boolean isVulnerable, String cvePublishedDate) {
        this.cveId = cveId;
        this.baseScore = baseScore;
        this.sourcePackageName = sourcePackageName;
        this.sourcePackageVersion = sourcePackageVersion;
        this.gardenlinuxVersion = gardenlinuxVersion;
        this.isVulnerable = isVulnerable;
        this.cvePublishedDate = cvePublishedDate;
    }

    public String getCveId() {
        return cveId;
    }

    public float getBaseScore() {
        return baseScore;
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
}