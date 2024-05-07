package io.gardenlinux.glvd.dto;

import java.util.Objects;

public class Readiness {

    private final String dbCheck;

    public Readiness(String dbCheck) {
        this.dbCheck = dbCheck;
    }

    public String getDbCheck() {
        return dbCheck;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Readiness readiness = (Readiness) o;
        return Objects.equals(dbCheck, readiness.dbCheck);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dbCheck);
    }

}
