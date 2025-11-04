package io.gardenlinux.glvd.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "triage")
public class Triage {

    @Id
    @Column(name = "cve_id", nullable = false)
    private String cveId;

    @Column(name = "source_package_name", nullable = false)
    private String sourcePackageName;

    @Column(name = "source_package_version", nullable = false)
    private String sourcePackageVersion;

    @Column(name = "triage_marked_as_resolved", nullable = false)
    private boolean triageMarkedAsResolved;

    @Column(name = "triage_date", nullable = false)
    private String triageDate;

    @Column(name = "triage_use_case", nullable = false)
    private String triageUseCase;

    @Column(name = "triage_description", nullable = false)
    private String triageDescription;

    @Column(name = "triage_gardenlinux_version", nullable = false)
    private String triageGardenLinuxVersion;

    @Column(name = "nvd_vulnerability_status", nullable = false)
    private String nvdVulnerabilityStatus;

    @Column(name = "nvd_cve_published_date", nullable = false)
    private String nvdCvePublishedDate;

    @Column(name = "nvd_cve_last_modified_date", nullable = false)
    private String nvdCveLastModifiedDate;

    @Column(name = "nvd_cve_description", nullable = false)
    private String nvdCveDescription;

    @Column(name = "nvd_cve_cvss_base_score", nullable = true)
    private Float nvdCveCvssBaseScore;

    public Triage() {
    }

    public Triage(String cveId, String sourcePackageName, String sourcePackageVersion, boolean triageMarkedAsResolved, String triageDate, String triageUseCase, String triageDescription, String triageGardenLinuxVersion, String nvdVulnerabilityStatus, String nvdCvePublishedDate, String nvdCveLastModifiedDate, String nvdCveDescription, Float nvdCveCvssBaseScore) {
        this.cveId = cveId;
        this.sourcePackageName = sourcePackageName;
        this.sourcePackageVersion = sourcePackageVersion;
        this.triageMarkedAsResolved = triageMarkedAsResolved;
        this.triageDate = triageDate;
        this.triageUseCase = triageUseCase;
        this.triageDescription = triageDescription;
        this.triageGardenLinuxVersion = triageGardenLinuxVersion;
        this.nvdVulnerabilityStatus = nvdVulnerabilityStatus;
        this.nvdCvePublishedDate = nvdCvePublishedDate;
        this.nvdCveLastModifiedDate = nvdCveLastModifiedDate;
        this.nvdCveDescription = nvdCveDescription;
        this.nvdCveCvssBaseScore = nvdCveCvssBaseScore;
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

    public boolean isTriageMarkedAsResolved() {
        return triageMarkedAsResolved;
    }

    public String getTriageDate() {
        return triageDate;
    }

    public String getTriageUseCase() {
        return triageUseCase;
    }

    public String getTriageDescription() {
        return triageDescription;
    }

    public String getTriageGardenLinuxVersion() {
        return triageGardenLinuxVersion;
    }

    public String getNvdVulnerabilityStatus() {
        return nvdVulnerabilityStatus;
    }

    public String getNvdCvePublishedDate() {
        return nvdCvePublishedDate;
    }

    public String getNvdCveLastModifiedDate() {
        return nvdCveLastModifiedDate;
    }

    public String getNvdCveDescription() {
        return nvdCveDescription;
    }

    public Float getNvdCveCvssBaseScore() {
        return nvdCveCvssBaseScore;
    }
}
