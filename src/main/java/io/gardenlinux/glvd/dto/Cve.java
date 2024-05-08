package io.gardenlinux.glvd.dto;

import java.util.List;

public record Cve(String id, String lastModified, String sourceIdentifier, String published, String vulnStatus,
                  List<Description> descriptions, Object metrics, List<Reference> references, List<Weakness> weaknesses,
                  List<Configuration> configurations) {

}

