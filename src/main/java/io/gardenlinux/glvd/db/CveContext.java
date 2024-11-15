package io.gardenlinux.glvd.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "cve_context")
public class CveContext {
    @Id
    @Column(name = "cve_id", nullable = false)
    private String cveId;

    @Column(name = "dist_id", nullable = false)
    private String distId;

    @Column(name = "create_date", nullable = false)
    private String createDate;

    @Column(name = "context_descriptor", nullable = false)
    private String contextDescriptor;

    @Column(name = "score_override", nullable = true)
    private Float scoreOverride;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "is_resolved", nullable = true)
    private Boolean isResolved;

    public CveContext() {
    }

    public CveContext(String cveId, String distId, String createDate, String contextDescriptor, Float scoreOverride, String description, Boolean isResolved) {
        this.cveId = cveId;
        this.distId = distId;
        this.createDate = createDate;
        this.contextDescriptor = contextDescriptor;
        this.scoreOverride = scoreOverride;
        this.description = description;
        this.isResolved = isResolved;
    }

    public String getCveId() {
        return cveId;
    }

    public String getDistId() {
        return distId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getContextDescriptor() {
        return contextDescriptor;
    }

    public Float getScoreOverride() {
        return scoreOverride;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getResolved() {
        return isResolved;
    }
}
