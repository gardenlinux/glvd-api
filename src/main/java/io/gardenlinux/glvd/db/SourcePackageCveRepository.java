package io.gardenlinux.glvd.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SourcePackageCveRepository extends JpaRepository<SourcePackageCve, String> {

    List<SourcePackageCve> findBySourcePackageName(@Param("source_package_name") String source_package_name, Sort sort);
    List<SourcePackageCve> findBySourcePackageNameAndSourcePackageVersion(@Param("source_package_name") String source_package_name, @Param("source_package_version") String source_package_version, Sort sort);
    List<SourcePackageCve> findByCveIdAndGardenlinuxVersion(@Param("cve_id") String cve_id, @Param("gardenlinux_version") String gardenlinux_version, Sort sort);

    List<SourcePackageCve> findByGardenlinuxVersion(@Param("gardenlinux_version") String gardenlinux_version, Sort sort);

    // would be nice if we did not need a native query here
    // is this possible in any other way?
    @Query(value = """
    SELECT * FROM sourcepackagecve
    WHERE source_package_name = ANY(:source_package_names ::TEXT[]) AND gardenlinux_version = :gardenlinux_version
    """, nativeQuery = true)
    List<SourcePackageCve> findBySourcePackageNameInAndGardenlinuxVersion(@Param("source_package_names") String source_package_names, @Param("gardenlinux_version") String gardenlinux_version);
}
