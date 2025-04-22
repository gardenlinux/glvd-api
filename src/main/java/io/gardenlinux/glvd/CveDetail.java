package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.CveDetails;
import io.gardenlinux.glvd.db.KernelCveDetails;

import java.util.ArrayList;
import java.util.List;

public record CveDetail(String cveId, String vulnStatus, String description, String cvePublishedDate,
                        String cveModifiedDate, String cveIngestedDate, List<String> kernelLtsVersion,
                        List<String> kernelFixedVersion, List<Boolean> kernelIsFixed,
                        List<Boolean> kernelIsRelevantSubsystem,
                        List<String> distro, List<String> distroVersion, List<Boolean> isVulnerable,
                        List<String> sourcePackageName, List<String> sourcePackageVersion, List<String> versionFixed,
                        Float baseScoreV40, Float baseScoreV31, Float baseScoreV30, Float baseScoreV2,
                        String vectorStringV40, String vectorStringV31, String vectorStringV30, String vectorStringV2) {


    static Boolean compareVersions(String mine, String yours) {
        var myKernelVersion = LinuxKernelVersion.fromRawVersion(mine);
        var yourKernelVersion = LinuxKernelVersion.fromRawVersion(yours);
        if (myKernelVersion.compareTo(yourKernelVersion) < 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    static boolean isKernelVersionVulnerable(KernelCveDetails k, String myKernelVersion) {
        for (String lts : k.getLtsVersion()) {
            if (myKernelVersion.startsWith(lts)) {
                var yourKernelVersions = k.getFixedVersion().stream().filter(s -> s != null && s.startsWith(lts)).toList();
                if (yourKernelVersions.size() == 1) {
                    return compareVersions(myKernelVersion, yourKernelVersions.getFirst());
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    static CveDetail fromKernelCve(KernelCveDetails k, KernelDistroVersions kernelDistroVersions) {
        KernelDistroInfo d = buildKernelInfoForGardenLinuxDistros(k, kernelDistroVersions);
        return new CveDetail(k.getCveId(), k.getVulnStatus(), k.getDescription(), k.getCvePublishedDate(),
                k.getCveModifiedDate(), k.getCveIngestedDate(), k.getLtsVersion(),
                k.getFixedVersion(), k.getIsFixed(), k.getIsRelevantSubsystem(), d.distros(), d.distroVersions(),
                d.isVulnerable(), d.sourcePackageNames(), d.kernelVersions(),
                k.getFixedVersion(), k.getBaseScoreV40(), k.getBaseScoreV31(), k.getBaseScoreV30(), k.getBaseScoreV2(),
                k.getVectorStringV40(), k.getVectorStringV31(),
                k.getVectorStringV30(), k.getVectorStringV2());
    }

    private static KernelDistroInfo buildKernelInfoForGardenLinuxDistros(KernelCveDetails k, KernelDistroVersions kernelDistroVersions) {
        var distros = new ArrayList<String>(kernelDistroVersions.distros().size());
        var distroVersions = new ArrayList<String>(kernelDistroVersions.distros().size());
        var sourcePackageNames = new ArrayList<String>(kernelDistroVersions.distros().size());
        var kernelVersions = new ArrayList<String>(kernelDistroVersions.distros().size());
        var vulnerability = new ArrayList<Boolean>(kernelDistroVersions.distros().size());
        for (int i = 0; i < kernelDistroVersions.distros().size(); i++) {
            if (kernelDistroVersions.distros().get(i).equals("gardenlinux")) {
                distros.add(kernelDistroVersions.distros().get(i));
                distroVersions.add(kernelDistroVersions.versions().get(i));
                sourcePackageNames.add(kernelDistroVersions.sourcePackageName().get(i));
                kernelVersions.add(kernelDistroVersions.kernelVersions().get(i));
                vulnerability.add(isKernelVersionVulnerable(k, kernelDistroVersions.kernelVersions().get(i)));
            }
        }
        return new KernelDistroInfo(distros, distroVersions, sourcePackageNames, kernelVersions, vulnerability);
    }

    static CveDetail fromDebianCve(CveDetails c) {
        return new CveDetail(c.getCveId(), c.getVulnStatus(), c.getDescription(), c.getCvePublishedDate(),
                c.getCveModifiedDate(), c.getCveIngestedDate(), null,
                null, null, null, c.getDistro(), c.getDistroVersion(),
                c.getIsVulnerable(), c.getSourcePackageName(), c.getSourcePackageVersion(),
                c.getVersionFixed(), c.getBaseScoreV40(), c.getBaseScoreV31(), c.getBaseScoreV30(),
                c.getBaseScoreV2(), c.getVectorStringV40(), c.getVectorStringV31(),
                c.getVectorStringV30(), c.getVectorStringV2());
    }

    private record KernelDistroInfo(ArrayList<String> distros, ArrayList<String> distroVersions,
                                    ArrayList<String> sourcePackageNames, ArrayList<String> kernelVersions,
                                    ArrayList<Boolean> isVulnerable) {
    }
}
