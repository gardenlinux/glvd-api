package io.gardenlinux.glvd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CvssMetricV2(String source, String type, CvssData cvssData, String baseSeverety,
                           double exploitabilityScore, double impactScore, boolean acInsufInfo,
                           boolean obtainAllPrivilege, boolean obtainUserPrivilege, boolean obtainOtherPrivilege,
                           boolean userInteractionRequired) {
}
