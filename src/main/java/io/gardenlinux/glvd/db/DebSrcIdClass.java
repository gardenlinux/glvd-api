package io.gardenlinux.glvd.db;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.io.Serializable;

public class DebSrcIdClass implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "dist_id")
    Integer distId;

    @Id
    @Column(name = "deb_source")
    String debSource;

}
