package io.gardenlinux.glvd.db;

import jakarta.persistence.*;

@Entity
@Table(name = "cve_context")
public class CveContext {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "cve_id", nullable = false)
    private String cveId;

    @Column(name = "dist_id", nullable = false)
    private Integer distId;

    @Column(name = "create_date", nullable = false)
    private String createDate;

    @Column(name = "use_case", nullable = false)
    private String useCase;

    @Column(name = "score_override", nullable = true)
    private Float scoreOverride;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "is_resolved", nullable = true)
    private Boolean isResolved;

    public CveContext() {
    }

    public CveContext(Integer id, String cveId, Integer distId, String createDate, String useCase, Float scoreOverride, String description, Boolean isResolved) {
        this.id = id;
        this.cveId = cveId;
        this.distId = distId;
        this.createDate = createDate;
        this.useCase = useCase;
        this.scoreOverride = scoreOverride;
        this.description = description;
        this.isResolved = isResolved;
    }

    public Integer getId() {
        return id;
    }

    public String getCveId() {
        return cveId;
    }

    public Integer getDistId() {
        return distId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getUseCase() {
        return useCase;
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
