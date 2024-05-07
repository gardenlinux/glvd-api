package io.gardenlinux.glvd.db;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity(name = "all_cve")
public class CveEntity {
    @Id
    @Column(name = "cve_id", nullable = false)
    private String id;

    @Column(name = "last_mod", nullable = false)
    @Nonnull
    private String lastModified;

    @Column(name = "data", nullable = false)
    @Nonnull
    private String data;

    public CveEntity() {
    }

    public CveEntity(String id, @Nonnull String lastModified, @Nonnull String data) {
        this.id = id;
        this.lastModified = lastModified;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    @Nonnull
    public String getLastModified() {
        return lastModified;
    }

    @Nonnull
    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CveEntity cveEntity = (CveEntity) o;
        return Objects.equals(id, cveEntity.id) && lastModified.equals(cveEntity.lastModified) && data.equals(cveEntity.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + lastModified.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }
}
