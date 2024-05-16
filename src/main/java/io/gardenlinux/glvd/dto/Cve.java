package io.gardenlinux.glvd.dto;

import java.util.List;

public record Cve(String id, String lastModified, String sourceIdentifier, String published, String vulnStatus,
                  List<Description> descriptions, Metrics metrics, List<Reference> references,
                  List<Weakness> weaknesses,
                  List<Configuration> configurations, String evaluatorSolution, List<VendorComment> vendorComments,
                  String evaluatorComment,
                  String evaluatorImpact, String cisaExploitAdd, String cisaActionDue, String cisaRequiredAction,
                  String cisaVulnerabilityName) {

}

