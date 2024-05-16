package io.gardenlinux.glvd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CpeMatch(String criteria, Deb deb, boolean vulnerable, String versionStartIncluding,
                       String versionEndExcluding, String matchCriteriaId) {

}
