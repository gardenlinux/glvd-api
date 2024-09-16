package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.SourcePackage;
import io.gardenlinux.glvd.db.SourcePackageCve;
import io.gardenlinux.glvd.db.SourcePackageCveRepository;
import io.gardenlinux.glvd.db.SourcePackageRepository;
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

    Logger logger = LoggerFactory.getLogger(GlvdService.class);

    public GlvdService(@Nonnull SourcePackageCveRepository sourcePackageCveRepository, @Nonnull SourcePackageRepository sourcePackageRepository) {
        this.sourcePackageCveRepository = sourcePackageCveRepository;
        this.sourcePackageRepository = sourcePackageRepository;
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

}
