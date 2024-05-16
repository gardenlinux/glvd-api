package io.gardenlinux.glvd.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Cve(String id, String lastModified, String sourceIdentifier, String published, String vulnStatus,
                  List<Description> descriptions, Metrics metrics, List<Reference> references, List<Weakness> weaknesses,
                  List<Configuration> configurations, String evaluatorSolution, List<VendorComment> vendorComments, String evaluatorComment, String evaluatorImact) {

}

