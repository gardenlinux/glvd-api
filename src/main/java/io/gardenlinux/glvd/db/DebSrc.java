package io.gardenlinux.glvd.db;

import jakarta.persistence.*;

@Entity
@Table(name = "debsrc")
@IdClass(DebSrcIdClass.class)
public class DebSrc {
    @Id
    @Column(name = "dist_id", nullable = false)
    private Integer distId;

    @Column(name = "last_mod", nullable = false)
    private String lastMod;

    @Id
    @Column(name = "deb_source", nullable = false)
    private String debSource;

    @Column(name = "deb_version", nullable = false)
    private String debVersion;

    public DebSrc() {
    }

    public DebSrc(Integer distId, String lastMod, String debSource, String debVersion) {
        this.distId = distId;
        this.lastMod = lastMod;
        this.debSource = debSource;
        this.debVersion = debVersion;
    }

    public Integer getDistId() {
        return distId;
    }

    public String getLastMod() {
        return lastMod;
    }

    public String getDebSource() {
        return debSource;
    }

    public String getDebVersion() {
        return debVersion;
    }
}
