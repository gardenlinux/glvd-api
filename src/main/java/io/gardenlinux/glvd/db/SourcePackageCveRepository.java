package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SourcePackageCveRepository extends JpaRepository<SourcePackageCve, String> {

    List<SourcePackageCve> findBySourcePackageName(@Param("source_package_name") String source_package_name);
    List<SourcePackageCve> findBySourcePackageNameAndSourcePackageVersion(@Param("source_package_name") String source_package_name, @Param("source_package_version") String source_package_version);
    List<SourcePackageCve> findByCveIdAndGardenlinuxVersion(@Param("cve_id") String cve_id, @Param("gardenlinux_version") String gardenlinux_version);

    List<SourcePackageCve> findByGardenlinuxVersion(@Param("gardenlinux_version") String gardenlinux_version);

    // would be nice if we did not need a native query here
    // is this possible in any other way?
    @Query(value = "select * from sourcepackagecve where source_package_name = ANY(:source_package_names ::TEXT[]) AND gardenlinux_version = :gardenlinux_version", nativeQuery = true)
    List<SourcePackageCve> findBySourcePackageNameInAndGardenlinuxVersion(@Param("source_package_names") String source_package_names, @Param("gardenlinux_version") String gardenlinux_version);

    @Query(value = """
            SELECT
                debsrc.deb_source AS source_package_name
            FROM
                dist_cpe
            INNER JOIN debsrc ON
                (debsrc.dist_id = dist_cpe.id)
            WHERE
                dist_cpe.cpe_product = :distro
                AND dist_cpe.cpe_version = :distroVersion
            ORDER BY
                debsrc.deb_source""", nativeQuery = true)
    List<String> packagesForDistribution(@Param("distro") String distro, @Param("distroVersion") String distroVersion);
}
