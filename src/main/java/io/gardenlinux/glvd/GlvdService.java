package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.*;
import io.gardenlinux.glvd.releasenotes.ReleaseNote;
import io.gardenlinux.glvd.releasenotes.ReleaseNoteGenerator;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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

    Logger logger = LoggerFactory.getLogger(GlvdService.class);

    public GlvdService(@Nonnull SourcePackageCveRepository sourcePackageCveRepository, @Nonnull SourcePackageRepository sourcePackageRepository, @Nonnull CveDetailsRepository cveDetailsRepository, @Nonnull CveContextRepository cveContextRepository, @Nonnull DistCpeRepository distCpeRepository, @Nonnull NvdExclusiveCveRepository nvdExclusiveCveRepository, @Nonnull DebSrcRepository debSrcRepository) {
        this.sourcePackageCveRepository = sourcePackageCveRepository;
        this.sourcePackageRepository = sourcePackageRepository;
        this.cveDetailsRepository = cveDetailsRepository;
        this.cveContextRepository = cveContextRepository;
        this.distCpeRepository = distCpeRepository;
        this.nvdExclusiveCveRepository = nvdExclusiveCveRepository;
        this.debSrcRepository = debSrcRepository;
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
        return sourcePackageCveRepository.findByGardenlinuxVersion(
                gardenlinuxVersion, determinePageAndSortFeatures(sortAndPageOptions)
        );
    }

    private String wrap(String input) {
        return "{" + input + "}";
    }

    public List<SourcePackageCve> getCveForPackages(String gardenlinuxVersion, String packages, SortAndPageOptions sortAndPageOptions) {
        return sourcePackageCveRepository.findBySourcePackageNameInAndGardenlinuxVersion(
                wrap(packages), gardenlinuxVersion, determinePageAndSortFeatures2(sortAndPageOptions)
        );
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

    public CveDetails getCveDetails(String cveId) {
        return cveDetailsRepository.findByCveId(cveId);
    }

    public List<CveContext> getCveContexts(String cveId) {
        return cveContextRepository.findByCveId(cveId);
    }

    public List<CveContext> getCveContextsForDist(String dist_id) {
        return cveContextRepository.findByDistId(Integer.valueOf(dist_id)).stream().filter(cveContext -> !cveContext.getContextDescriptor().equalsIgnoreCase("dummy")).toList();
    }

    public String distVersionToId(String version) {
        return distCpeRepository.getByCpeVersion(version).getId();
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
        var v = new GardenLinuxVersion(gardenlinuxVersion);
        var cvesOldVersion = getCveForDistribution(v.previousPatchVersion(), new SortAndPageOptions("cveId", "ASC", null, null));
        var cvesNewVersion = getCveForDistribution(v.printVersion(), new SortAndPageOptions("cveId", "ASC", null, null));
        var resolvedInNew = getCveContextsForDist(distVersionToId(gardenlinuxVersion)).stream().filter(CveContext::getResolved).map(CveContext::getCveId).toList();
        var packagesOld = sourcePackagesByGardenLinuxVersion(v.previousPatchVersion());
        var packagesNew = sourcePackagesByGardenLinuxVersion(v.printVersion());

        return new ReleaseNoteGenerator(v, cvesOldVersion, cvesNewVersion, resolvedInNew, packagesOld, packagesNew).generate();
    }

}
