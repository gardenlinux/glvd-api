package io.gardenlinux.glvd.dto;

public record CvssMetricV2(String source, String type, CvssData cvssData, String baseSeverety,
                           double exploitabilityScore, double impactScore, boolean acInsufInfo,
                           boolean obtainAllPrivilege, boolean obtainUserPrivilege, boolean obtainOtherPrivilege,
                           boolean userInteractionRequired, String baseSeverity) {
}
