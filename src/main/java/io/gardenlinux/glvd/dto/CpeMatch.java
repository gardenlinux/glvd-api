package io.gardenlinux.glvd.dto;

public record CpeMatch(String criteria, Deb deb, boolean vulnerable, String versionStartIncluding,
                       String versionEndExcluding, String matchCriteriaId) {

}
