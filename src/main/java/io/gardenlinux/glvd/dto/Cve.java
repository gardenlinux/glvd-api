package io.gardenlinux.glvd.dto;

import jakarta.annotation.Nonnull;

import java.util.Objects;

public class Cve {
    private String id;
    @Nonnull
    private String lastModified;
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

        Cve Cve = (Cve) o;
        return Objects.equals(id, Cve.id) && lastModified.equals(Cve.lastModified) && data.equals(Cve.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + lastModified.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }
}
