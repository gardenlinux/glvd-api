package io.gardenlinux.glvd;

import java.util.List;

public record CveDetail(String cveId, String vulnStatus, String description, String cvePublishedDate,
                        String cveModifiedDate, String cveIngestedDate, List<String> ltsVersion,
                        List<String> fixedVersion, List<Boolean> isFixed, List<Boolean> isRelevantSubsystem,
                        List<String> distro, List<String> distroVersion, List<Boolean> isVulnerable,
                        List<String> sourcePackageName, List<String> sourcePackageVersion, List<String> versionFixed,
                        Float baseScoreV40, Float baseScoreV31, Float baseScoreV30, Float baseScoreV2,
                        String vectorStringV40, String vectorStringV31, String vectorStringV30, String vectorStringV2) {
}
