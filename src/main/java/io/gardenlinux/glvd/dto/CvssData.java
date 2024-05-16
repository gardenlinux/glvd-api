package io.gardenlinux.glvd.dto;

public record CvssData(String version, String vectorString, String accessVector, String attackVector,
                       String attackComplexity, String accessComplexity, String privilegesRequired,
                       String userInteraction, String scope, String baseSeverity,
                       String authentication, String confidentialityImpact, String integrityImpact,
                       String availabilityImpact, double baseScore) {
}
