package io.gardenlinux.glvd.dto;

import java.util.Objects;

public class Deb {
    private String versionLatest;
    private String versionEndExcluding;
    private String cvssSeverity;

    public Deb() {
    }

    public Deb(String versionLatest, String versionEndExcluding, String cvssSeverity) {
        this.versionLatest = versionLatest;
        this.versionEndExcluding = versionEndExcluding;
        this.cvssSeverity = cvssSeverity;
    }

    public String getVersionLatest() {
        return versionLatest;
    }

    public String getVersionEndExcluding() {
        return versionEndExcluding;
    }

    public String getCvssSeverity() {
        return cvssSeverity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Deb deb = (Deb) o;
        return Objects.equals(versionLatest, deb.versionLatest) && Objects.equals(versionEndExcluding, deb.versionEndExcluding) && Objects.equals(cvssSeverity, deb.cvssSeverity);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(versionLatest);
        result = 31 * result + Objects.hashCode(versionEndExcluding);
        result = 31 * result + Objects.hashCode(cvssSeverity);
        return result;
    }

    @Override
    public String toString() {
        return "Deb{" +
                "versionLatest='" + versionLatest + '\'' +
                ", versionEndExcluding='" + versionEndExcluding + '\'' +
                ", cvssSeverity='" + cvssSeverity + '\'' +
                '}';
    }
}
