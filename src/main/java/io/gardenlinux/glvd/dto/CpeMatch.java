package io.gardenlinux.glvd.dto;

import java.util.Objects;

public class CpeMatch {
    private String criteria;
    private Deb deb;
    private boolean vulnerable;
    private String versionStartIncluding;
    private String versionEndExcluding;
    private String matchCriteriaId;

    public CpeMatch() {
    }


    public CpeMatch(String criteria, Deb deb, boolean vulnerable, String versionStartIncluding, String versionEndExcluding, String matchCriteriaId) {
        this.criteria = criteria;
        this.deb = deb;
        this.vulnerable = vulnerable;
        this.versionStartIncluding = versionStartIncluding;
        this.versionEndExcluding = versionEndExcluding;
        this.matchCriteriaId = matchCriteriaId;
    }

    public String getVersionStartIncluding() {
        return versionStartIncluding;
    }

    public String getCriteria() {
        return criteria;
    }

    public Deb getDeb() {
        return deb;
    }

    public boolean isVulnerable() {
        return vulnerable;
    }

    public String getVersionEndExcluding() {
        return versionEndExcluding;
    }

    public String getMatchCriteriaId() {
        return matchCriteriaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CpeMatch cpeMatch = (CpeMatch) o;
        return vulnerable == cpeMatch.vulnerable && Objects.equals(criteria, cpeMatch.criteria) && Objects.equals(deb, cpeMatch.deb) && Objects.equals(versionStartIncluding, cpeMatch.versionStartIncluding) && Objects.equals(versionEndExcluding, cpeMatch.versionEndExcluding) && Objects.equals(matchCriteriaId, cpeMatch.matchCriteriaId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(criteria);
        result = 31 * result + Objects.hashCode(deb);
        result = 31 * result + Boolean.hashCode(vulnerable);
        result = 31 * result + Objects.hashCode(versionStartIncluding);
        result = 31 * result + Objects.hashCode(versionEndExcluding);
        result = 31 * result + Objects.hashCode(matchCriteriaId);
        return result;
    }

    @Override
    public String toString() {
        return "CpeMatch{" +
                "criteria='" + criteria + '\'' +
                ", deb=" + deb +
                ", vulnerable=" + vulnerable +
                ", versionStartIncluding='" + versionStartIncluding + '\'' +
                ", versionEndExcluding='" + versionEndExcluding + '\'' +
                ", matchCriteriaId='" + matchCriteriaId + '\'' +
                '}';
    }
}
