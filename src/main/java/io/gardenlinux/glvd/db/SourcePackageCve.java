package io.gardenlinux.glvd.db;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class SourcePackageCve {

    @Id
    @Column(name = "cve_id", nullable = false)
    private String id;

    @Column(name = "cve_published_date", nullable = false)
    @Nonnull
    private String cvePublishedDate;

    @Column(name = "source_package", nullable = false)
    @Nonnull
    private String sourcePackage;

    public SourcePackageCve() {
    }

    public SourcePackageCve(String id, @Nonnull String cvePublishedDate, @Nonnull String sourcePackage) {
        this.id = id;
        this.cvePublishedDate = cvePublishedDate;
        this.sourcePackage = sourcePackage;
    }

    public String getId() {
        return id;
    }

    @Nonnull
    public String getCvePublishedDate() {
        return cvePublishedDate;
    }

    @Nonnull
    public String getSourcePackage() {
        return sourcePackage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourcePackageCve that = (SourcePackageCve) o;
        return Objects.equals(id, that.id) && cvePublishedDate.equals(that.cvePublishedDate) && sourcePackage.equals(that.sourcePackage);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + cvePublishedDate.hashCode();
        result = 31 * result + sourcePackage.hashCode();
        return result;
    }
}
