package io.gardenlinux.glvd.dto;

import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Objects;

public class Cve {

    private String id;

    @Nonnull
    private String lastModified;

    @Nonnull
    private String sourceIdentifier;

    private String published;

    private String vulnStatus;

    private List<Description> descriptions;

    private Object metrics;

    private List<Reference> references;

    private List<Weakness> weaknesses;

    private List<Configuration> configurations;


    public Cve() {
    }

    public Cve(String id, @Nonnull String lastModified, @Nonnull String sourceIdentifier, String published, String vulnStatus, List<Description> descriptions, Object metrics, List<Reference> references, List<Weakness> weaknesses, List<Configuration> configurations) {
        this.id = id;
        this.lastModified = lastModified;
        this.sourceIdentifier = sourceIdentifier;
        this.published = published;
        this.vulnStatus = vulnStatus;
        this.descriptions = descriptions;
        this.metrics = metrics;
        this.references = references;
        this.weaknesses = weaknesses;
        this.configurations = configurations;
    }

    public String getId() {
        return id;
    }

    @Nonnull
    public String getLastModified() {
        return lastModified;
    }

    @Nonnull
    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    public String getPublished() {
        return published;
    }

    public String getVulnStatus() {
        return vulnStatus;
    }

    public List<Description> getDescriptions() {
        return descriptions;
    }

    public Object getMetrics() {
        return metrics;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public List<Weakness> getWeaknesses() {
        return weaknesses;
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cve cve = (Cve) o;
        return Objects.equals(id, cve.id) && lastModified.equals(cve.lastModified) && sourceIdentifier.equals(cve.sourceIdentifier) && Objects.equals(published, cve.published) && Objects.equals(vulnStatus, cve.vulnStatus) && Objects.equals(descriptions, cve.descriptions) && Objects.equals(metrics, cve.metrics) && Objects.equals(references, cve.references) && Objects.equals(weaknesses, cve.weaknesses) && Objects.equals(configurations, cve.configurations);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + lastModified.hashCode();
        result = 31 * result + sourceIdentifier.hashCode();
        result = 31 * result + Objects.hashCode(published);
        result = 31 * result + Objects.hashCode(vulnStatus);
        result = 31 * result + Objects.hashCode(descriptions);
        result = 31 * result + Objects.hashCode(metrics);
        result = 31 * result + Objects.hashCode(references);
        result = 31 * result + Objects.hashCode(weaknesses);
        result = 31 * result + Objects.hashCode(configurations);
        return result;
    }

    @Override
    public String toString() {
        return "Cve{" +
                "id='" + id + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", sourceIdentifier='" + sourceIdentifier + '\'' +
                ", published='" + published + '\'' +
                ", vulnStatus='" + vulnStatus + '\'' +
                ", descriptions=" + descriptions +
                ", metrics=" + metrics +
                ", references=" + references +
                ", weaknesses=" + weaknesses +
                ", configurations=" + configurations +
                '}';
    }
}
