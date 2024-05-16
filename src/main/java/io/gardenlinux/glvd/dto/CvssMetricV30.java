package io.gardenlinux.glvd.dto;

public record CvssMetricV30(String source, String type, CvssData cvssData, double exploitabilityScore, double impactScore) {
}
