package io.gardenlinux.glvd.db;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sourcepackage")
public class SourcePackage {

    @Id
    @Column(name = "source_package_name", nullable = false)
    private String sourcePackageName;

    @Column(name = "source_package_version", nullable = false)
    private String sourcePackageVersion;

    @Column(name = "gardenlinux_version", nullable = false)
    private String gardenlinuxVersion;

    public SourcePackage() {
    }

    public SourcePackage(String sourcePackageName, String sourcePackageVersion, String gardenlinuxVersion) {
        this.sourcePackageName = sourcePackageName;
        this.sourcePackageVersion = sourcePackageVersion;
        this.gardenlinuxVersion = gardenlinuxVersion;
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
}