package io.gardenlinux.glvd.db;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "sourcepackagecve")
public class SourcePackageCve {

    @Id
    @Column(name = "cve_id", nullable = false)
    private String id;

    @Column(name = "deb_source", nullable = false)
    @Nonnull
    private String debSource;

    @Column(name = "deb_version", nullable = false)
    @Nonnull
    private String debVersion;

    @Column(name = "debsec_vulnerable", nullable = false)
    @Nonnull
    private String debsecVulnerable;

    public SourcePackageCve() {
    }

    public SourcePackageCve(String id, @Nonnull String debSource, @Nonnull String debVersion, @Nonnull String debsecVulnerable) {
        this.id = id;
        this.debSource = debSource;
        this.debVersion = debVersion;
        this.debsecVulnerable = debsecVulnerable;
    }

    public String getId() {
        return id;
    }

    @Nonnull
    public String getDebSource() {
        return debSource;
    }

    @Nonnull
    public String getDebVersion() {
        return debVersion;
    }

    @Nonnull
    public String getDebsecVulnerable() {
        return debsecVulnerable;
    }
}
