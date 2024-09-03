package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PackagesRepository extends JpaRepository<PackageEntity, String> {

    @Query(value = """
            SELECT
                all_cve.cve_id AS cve_id, deb_cve.deb_source AS deb_source, deb_cve.deb_version AS deb_version, deb_cve.debsec_vulnerable AS debsec_vulnerable
            FROM
                all_cve
                INNER JOIN deb_cve USING (cve_id)
                INNER JOIN dist_cpe ON (deb_cve.dist_id = dist_cpe.id)
            WHERE
                deb_cve.deb_source = :sourcePackage
                AND deb_cve.debsec_vulnerable = TRUE
            ORDER BY
                all_cve.cve_id
            """, nativeQuery = true)
    List<PackageEntity> packageWithVulnerabilities(@Param("sourcePackage") String sourcePackage);

    @Query(value = """
            SELECT
                all_cve.cve_id AS cve_id, deb_cve.deb_source AS deb_source, deb_cve.deb_version AS deb_version, deb_cve.debsec_vulnerable AS debsec_vulnerable
            FROM
                all_cve
                INNER JOIN deb_cve USING (cve_id)
                INNER JOIN dist_cpe ON (deb_cve.dist_id = dist_cpe.id)
            WHERE
                deb_cve.deb_source = :sourcePackage
                AND deb_cve.deb_version = :sourcePackageVersion
                AND deb_cve.debsec_vulnerable = TRUE
            ORDER BY
                all_cve.cve_id
            """, nativeQuery = true)
    List<PackageEntity> packageWithVulnerabilitiesByVersion(@Param("sourcePackage") String sourcePackage, @Param("sourcePackageVersion") String sourcePackageVersion);

}
