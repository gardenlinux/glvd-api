package io.gardenlinux.glvd.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity(name = "debsrc")
public class PackageEntity {

    @Id
    @Column(name = "cve_id", nullable = false)
    private String cveId;

    @Column(name = "deb_source", nullable = false)
    private String debSource;

    @Column(name = "deb_version", nullable = false)
    private String debVersion;

    @Column(name = "debsec_vulnerable", nullable = false)
    private String debsecVulnerable;

    public PackageEntity() {
    }

    public PackageEntity(String cveId, String debSource, String debVersion, String debsecVulnerable) {
        this.cveId = cveId;
        this.debSource = debSource;
        this.debVersion = debVersion;
        this.debsecVulnerable = debsecVulnerable;
    }

    public String getCveId() {
        return cveId;
    }

    public String getDebSource() {
        return debSource;
    }

    public String getDebVersion() {
        return debVersion;
    }

    public String getDebsecVulnerable() {
        return debsecVulnerable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackageEntity that = (PackageEntity) o;
        return Objects.equals(cveId, that.cveId) && Objects.equals(debSource, that.debSource) && Objects.equals(debVersion, that.debVersion) && Objects.equals(debsecVulnerable, that.debsecVulnerable);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(cveId);
        result = 31 * result + Objects.hashCode(debSource);
        result = 31 * result + Objects.hashCode(debVersion);
        result = 31 * result + Objects.hashCode(debsecVulnerable);
        return result;
    }
}
