package io.gardenlinux.glvd.dto;

public record CvssMetricV31(String source, String type, CvssData cvssData,
                            double exploitabilityScore, double impactScore) {
}
