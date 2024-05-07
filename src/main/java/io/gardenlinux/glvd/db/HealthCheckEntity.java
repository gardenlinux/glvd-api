package io.gardenlinux.glvd.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class HealthCheckEntity {

    @Id
    private String id;

}
