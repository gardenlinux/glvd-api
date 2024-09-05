package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CveRepository extends JpaRepository<CveEntity, String> {

    @Query(value = """
             SELECT
                 deb_cve.deb_source AS source_package,
                 all_cve.cve_id AS cve_id,
                 all_cve."data" ->> 'published' AS cve_published_date
             FROM
                 all_cve
                 INNER JOIN deb_cve USING (cve_id)
                 INNER JOIN dist_cpe ON (deb_cve.dist_id = dist_cpe.id)
             WHERE
                dist_cpe.cpe_product = :distro AND
                dist_cpe.cpe_version = :distroVersion AND
                deb_cve.debsec_vulnerable = TRUE
             ORDER BY
                     all_cve.cve_id
            """, nativeQuery = true)
    List<String> cvesForDistribution(@Param("distro") String distro, @Param("distroVersion") String distroVersion);

    @Query(value = """
            SELECT
                deb_cve.deb_source AS source_package,
                all_cve.cve_id AS cve_id,
                all_cve."data" ->> 'published' AS cve_published_date
            FROM
                all_cve
                INNER JOIN deb_cve USING (cve_id)
                INNER JOIN dist_cpe ON (deb_cve.dist_id = dist_cpe.id)
            WHERE
                dist_cpe.cpe_product = :distro AND
                dist_cpe.cpe_version = :distroVersion AND
                deb_cve.deb_source = ANY(:packages ::TEXT[]) AND
                deb_cve.debsec_vulnerable = TRUE
            ORDER BY
                all_cve.cve_id
            """, nativeQuery = true)
    List<String> cvesForPackageList(@Param("distro") String distro, @Param("distroVersion") String distroVersion, @Param("packages") String packages);

    @Query(value = """
            SELECT
                debsrc.deb_source
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
