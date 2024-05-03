package io.gardenlinux.glvd;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity(name = "all_cve")
public class Cve {
    @Id
    @Column(name = "cve_id", nullable = false)
    private String id;

    @Column(name = "last_mod", nullable = false)
    @Nonnull
    private String lastModified;

    @Column(name = "data", nullable = false)
    @Nonnull
    private String data;

    public Cve() {
    }

    public Cve(String id, @Nonnull String lastModified, @Nonnull String data) {
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

        Cve cve = (Cve) o;
        return Objects.equals(id, cve.id) && lastModified.equals(cve.lastModified) && data.equals(cve.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + lastModified.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }
}
