package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.*;
import io.gardenlinux.glvd.exceptions.CveNotKnownException;
import io.gardenlinux.glvd.releasenotes.ReleaseNote;
import io.gardenlinux.glvd.releasenotes.ReleaseNoteGenerator;
import io.gardenlinux.glvd.version.ThreeDigitGardenLinuxVersion;
import io.gardenlinux.glvd.version.TwoDigitGardenLinuxVersion;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class GlvdService {

    @Nonnull
    private final SourcePackageCveRepository sourcePackageCveRepository;

    @Nonnull
    private final SourcePackageRepository sourcePackageRepository;

    @Nonnull
    private final CveDetailsRepository cveDetailsRepository;

    @Nonnull
    private final CveContextRepository cveContextRepository;

    @Nonnull
    private final DistCpeRepository distCpeRepository;

    @Nonnull
    private final NvdExclusiveCveRepository nvdExclusiveCveRepository;

    @Nonnull
    private final DebSrcRepository debSrcRepository;

    @Nonnull
    private final KernelCveRepository kernelCveRepository;

    @Nonnull
    private final KernelCveDetailsRepository kernelCveDetailsRepository;

    @Nonnull
    private final NvdCveRepository nvdCveRepository;

    Logger logger = LoggerFactory.getLogger(GlvdService.class);

    public GlvdService(@Nonnull SourcePackageCveRepository sourcePackageCveRepository, @Nonnull SourcePackageRepository sourcePackageRepository, @Nonnull CveDetailsRepository cveDetailsRepository, @Nonnull CveContextRepository cveContextRepository, @Nonnull DistCpeRepository distCpeRepository, @Nonnull NvdExclusiveCveRepository nvdExclusiveCveRepository, @Nonnull DebSrcRepository debSrcRepository, @Nonnull KernelCveRepository kernelCveRepository, @Nonnull KernelCveDetailsRepository kernelCveDetailsRepository, @Nonnull NvdCveRepository nvdCveRepository) {
        this.sourcePackageCveRepository = sourcePackageCveRepository;
        this.sourcePackageRepository = sourcePackageRepository;
        this.cveDetailsRepository = cveDetailsRepository;
        this.cveContextRepository = cveContextRepository;
        this.distCpeRepository = distCpeRepository;
        this.nvdExclusiveCveRepository = nvdExclusiveCveRepository;
        this.debSrcRepository = debSrcRepository;
        this.kernelCveRepository = kernelCveRepository;
        this.kernelCveDetailsRepository = kernelCveDetailsRepository;
        this.nvdCveRepository = nvdCveRepository;
    }

    private Pageable determinePageAndSortFeatures(SortAndPageOptions sortAndPageOptions) {
        var sort = Sort.by(Sort.Direction.valueOf(sortAndPageOptions.sortOrder()), sortAndPageOptions.sortBy());
        if (!StringUtils.isEmpty(sortAndPageOptions.pageNumber()) && !StringUtils.isEmpty(sortAndPageOptions.pageSize())) {
            try {
                var num = Integer.parseInt(sortAndPageOptions.pageNumber());
                var size = Integer.parseInt(sortAndPageOptions.pageSize());
                return PageRequest.of(num, size, sort);
            } catch (NumberFormatException e) {
                // fall through, don't page
                logger.warn("Could not parse paging parameters", e);
            }
        }

        return Pageable.unpaged(sort);
    }

    // native query does not support sorting
    private Pageable determinePageAndSortFeatures2(SortAndPageOptions sortAndPageOptions) {
        if (!StringUtils.isEmpty(sortAndPageOptions.pageNumber()) && !StringUtils.isEmpty(sortAndPageOptions.pageSize())) {
            try {
                var num = Integer.parseInt(sortAndPageOptions.pageNumber());
                var size = Integer.parseInt(sortAndPageOptions.pageSize());
                return PageRequest.of(num, size);
            } catch (NumberFormatException e) {
                // fall through, don't page
                logger.warn("Could not parse paging parameters", e);
            }
        }

        return Pageable.unpaged();
    }

    public List<SourcePackageCve> getCveForDistribution(String gardenlinuxVersion, SortAndPageOptions sortAndPageOptions) {
        var cvesExcludingKernel = sourcePackageCveRepository.findByGardenlinuxVersion(
                gardenlinuxVersion, determinePageAndSortFeatures(sortAndPageOptions)
        );

        var kernelCves = kernelCveRepository.findByGardenlinuxVersion(gardenlinuxVersion)
                .stream()
                .map(kernelCve -> new SourcePackageCve(kernelCve.getCveId(), kernelCve.getSourcePackageName(), kernelCve.getSourcePackageVersion(), kernelCve.getGardenlinuxVersion(), kernelCve.isVulnerable(), kernelCve.getCvePublishedDate(), kernelCve.getCveLastModifiedDate(), kernelCve.getCveLastIngestedDate(), kernelCve.getBaseScore(), kernelCve.getVectorString(), kernelCve.getBaseScoreV40(), kernelCve.getBaseScoreV31(), kernelCve.getBaseScoreV30(), kernelCve.getBaseScoreV2(), kernelCve.getVectorStringV40(), kernelCve.getVectorStringV31(), kernelCve.getVectorStringV30(), kernelCve.getVectorStringV2()))
                .toList();

        return Stream.concat(cvesExcludingKernel.stream(), kernelCves.stream()).toList();
    }

    public List<KernelCve> kernelCvesForGardenLinuxVersion(String gardenlinuxVersion) {
        return kernelCveRepository.findByGardenlinuxVersion(gardenlinuxVersion);
    }

    private String wrap(String input) {
        return "{" + input + "}";
    }

    private boolean packageListContainsKernel(String packages) {
        return packages.equals("linux") || packages.startsWith("linux,") || packages.contains(",linux,") || packages.endsWith(",linux");
    }

    public List<SourcePackageCve> getCveForPackages(String gardenlinuxVersion, String packages, SortAndPageOptions sortAndPageOptions) {
        List<SourcePackageCve> kernelCves = new ArrayList<>();
        if (packageListContainsKernel(packages)) {
            kernelCves = kernelCveRepository.findByGardenlinuxVersion(gardenlinuxVersion)
                    .stream()
                    .map(kernelCve -> new SourcePackageCve(kernelCve.getCveId(), kernelCve.getSourcePackageName(), kernelCve.getSourcePackageVersion(), kernelCve.getGardenlinuxVersion(), kernelCve.isVulnerable(), kernelCve.getCvePublishedDate(), kernelCve.getCveLastModifiedDate(), kernelCve.getCveLastIngestedDate(), kernelCve.getBaseScore(), kernelCve.getVectorString(), kernelCve.getBaseScoreV40(), kernelCve.getBaseScoreV31(), kernelCve.getBaseScoreV30(), kernelCve.getBaseScoreV2(), kernelCve.getVectorStringV40(), kernelCve.getVectorStringV31(), kernelCve.getVectorStringV30(), kernelCve.getVectorStringV2()))
                    .toList();

        }

        var cvesExcludingKernel = sourcePackageCveRepository.findBySourcePackageNameInAndGardenlinuxVersion(
                wrap(packages), gardenlinuxVersion, determinePageAndSortFeatures2(sortAndPageOptions)
        );

        return Stream.concat(cvesExcludingKernel.stream(), kernelCves.stream()).toList();
    }

    public List<SourcePackage> getPackagesForDistro(String gardenlinuxVersion, SortAndPageOptions sortAndPageOptions) {
        return sourcePackageRepository.findByGardenlinuxVersion(
                gardenlinuxVersion, determinePageAndSortFeatures(sortAndPageOptions)
        );
    }

    public List<SourcePackageCve> getPackageWithVulnerabilities(String sourcePackage, SortAndPageOptions sortAndPageOptions) {
        return sourcePackageCveRepository.findBySourcePackageName(
                sourcePackage, determinePageAndSortFeatures(sortAndPageOptions)
        );
    }

    public List<SourcePackageCve> getPackageWithVulnerabilitiesByVersion(String sourcePackage, String sourcePackageVersion, SortAndPageOptions sortAndPageOptions) {
        return sourcePackageCveRepository.findBySourcePackageNameAndSourcePackageVersion(
                sourcePackage, sourcePackageVersion, determinePageAndSortFeatures(sortAndPageOptions)
        );
    }

    public List<SourcePackageCve> getPackagesByVulnerability(String gardenlinuxVersion, String cveId, SortAndPageOptions sortAndPageOptions) {
        return sourcePackageCveRepository.findByCveIdAndGardenlinuxVersion(
                cveId, gardenlinuxVersion, determinePageAndSortFeatures(sortAndPageOptions)
        );
    }

    public CveDetail getCveDetails(String cveId) {
        // special case handling for kernel cves
        // the information provided by debian does not apply to Garden Linux because we maintain our own builds of LTS kernels
        // for this reason, is a cve is looked up, check first if it is a kernel cve and prefer that over what we have
        var kernelCveDetailsOptional = kernelCveDetailsRepository.findByCveId(cveId);

        var debianCveDetails = cveDetailsRepository.findByCveId(cveId);

        if (debianCveDetails == null) {
            var nvdCve = nvdCveRepository.findByCveId(cveId);
            if (nvdCve != null) {
                return CveDetail.fromNvdCve(nvdCve);
            } else {
                throw new CveNotKnownException(cveId + " is not in the GLVD database. " +
                        "It might either be very new and not yet be available in GLVD, or the ID might be misspelled.");
            }
        }

        if (kernelCveDetailsOptional.isPresent()) {
            var kernelCveDetails = kernelCveDetailsOptional.get();
            return CveDetail.fromKernelCve(kernelCveDetails, new KernelDistroVersions(debianCveDetails.getDistro(), debianCveDetails.getDistroVersion(), debianCveDetails.getSourcePackageName(), debianCveDetails.getSourcePackageVersion()));
        }

        return CveDetail.fromDebianCve(debianCveDetails);
    }

    public List<CveContext> getCveContexts(String cveId) {
        return cveContextRepository.findByCveId(cveId);
    }

    public List<CveContext> getCveContextsForDist(String dist_id) {
        return cveContextRepository.findByDistId(Integer.valueOf(dist_id)).stream().filter(cveContext -> !cveContext.getUseCase().equalsIgnoreCase("dummy")).toList();
    }

    public List<CveContext> getCveContextsForGardenLinuxVersion(String gardenlinuxVersion) {
        return getCveContextsForDist(distVersionToId(gardenlinuxVersion));
    }

    public String distVersionToId(String version) {
        return distCpeRepository.getByCpeVersion(version).getId();
    }

    public List<String> allGardenLinuxVersions() {
        return distCpeRepository.findAll().stream()
                .filter(distCpe -> distCpe.getCpeProduct().equalsIgnoreCase("gardenlinux"))
                .map(DistCpe::getCpeVersion)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .sorted()
                .toList();
    }

    public Iterable<NvdExclusiveCve> getAllNvdExclusiveCve() {
        return nvdExclusiveCveRepository.findAll();
    }

    public List<DebSrc> sourcePackagesByGardenLinuxVersion(String version) {
        return debSrcRepository.findByDistId(Integer.parseInt(distVersionToId(version)));
    }

    public ReleaseNote releaseNote(final String gardenlinuxVersion) {
        if (gardenlinuxVersion.endsWith(".0")) {
            return new ReleaseNote(gardenlinuxVersion, List.of());
        }
        var v = new TwoDigitGardenLinuxVersion(gardenlinuxVersion);

        var packagesNew = sourcePackagesByGardenLinuxVersion(v.printVersion());

        // We get an empty list if the new version is not yet present in glvd which creates a useless diff
        // This should not happen in a normal release process because the 'new' version should be ingested into glvd when we get here
        if (packagesNew.isEmpty()) {
            return new ReleaseNote(gardenlinuxVersion, List.of());
        }

        var cvesOldVersion = getCveForDistribution(v.previousPatchVersion(), new SortAndPageOptions("cveId", "ASC", null, null));
        var cvesNewVersion = getCveForDistribution(v.printVersion(), new SortAndPageOptions("cveId", "ASC", null, null));
        var resolvedInNew = getCveContextsForDist(distVersionToId(gardenlinuxVersion)).stream().filter(CveContext::getResolved).map(CveContext::getCveId).toList();
        var packagesOld = sourcePackagesByGardenLinuxVersion(v.previousPatchVersion());

        return new ReleaseNoteGenerator(v, cvesOldVersion, cvesNewVersion, resolvedInNew, packagesOld, packagesNew).generate();
    }

    public ReleaseNote releaseNote2(final String gardenlinuxVersion) {
        if (gardenlinuxVersion.endsWith(".0.0")) {
            return new ReleaseNote(gardenlinuxVersion, List.of());
        }
        var v = new ThreeDigitGardenLinuxVersion(gardenlinuxVersion);

        var packagesNew = sourcePackagesByGardenLinuxVersion(v.printVersion());

        // We get an empty list if the new version is not yet present in glvd which creates a useless diff
        // This should not happen in a normal release process because the 'new' version should be ingested into glvd when we get here
        if (packagesNew.isEmpty()) {
            return new ReleaseNote(gardenlinuxVersion, List.of());
        }

        var cvesOldVersion = getCveForDistribution(v.previousMinorVersion(), new SortAndPageOptions("cveId", "ASC", null, null));
        var cvesNewVersion = getCveForDistribution(v.printVersion(), new SortAndPageOptions("cveId", "ASC", null, null));
        var resolvedInNew = getCveContextsForDist(distVersionToId(gardenlinuxVersion)).stream().filter(CveContext::getResolved).map(CveContext::getCveId).toList();
        var packagesOld = sourcePackagesByGardenLinuxVersion(v.previousMinorVersion());

        return new ReleaseNoteGenerator(v, cvesOldVersion, cvesNewVersion, resolvedInNew, packagesOld, packagesNew).generate();
    }

}
