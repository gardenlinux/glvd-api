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
                 dist_cpe.cpe_product = :product AND
                 dist_cpe.deb_codename = :codename AND
                 deb_cve.debsec_vulnerable = TRUE
             ORDER BY
                 all_cve.cve_id
            """, nativeQuery = true)
    List<String> cvesForDistribution(@Param("product") String product, @Param("codename") String codename);

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
                 dist_cpe.cpe_product = :product AND
                 dist_cpe.cpe_version = :version AND
                 deb_cve.debsec_vulnerable = TRUE
             ORDER BY
                     all_cve.cve_id
            """, nativeQuery = true)
    List<String> cvesForDistributionVersion(@Param("product") String product, @Param("version") String version);

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
                 dist_cpe.cpe_product = :product AND
                 dist_cpe.deb_codename = :codename AND
                 deb_cve.deb_source = ANY(:packages ::TEXT[]) AND
                 deb_cve.debsec_vulnerable = TRUE
            ORDER BY
                all_cve.cve_id
            """, nativeQuery = true)
    List<String> cvesForPackageList(@Param("product") String product, @Param("codename") String codename, @Param("packages") String packages);

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
                dist_cpe.cpe_product = :product AND
                dist_cpe.cpe_version = :version AND
                deb_cve.deb_source = ANY(:packages ::TEXT[]) AND
                 deb_cve.debsec_vulnerable = TRUE
            ORDER BY
                all_cve.cve_id
            """, nativeQuery = true)
    List<String> cvesForPackageListVersion(@Param("product") String product, @Param("version") String version, @Param("packages") String packages);


    @Query(value = """
            SELECT
                debsrc.deb_source
            FROM
                dist_cpe
            INNER JOIN debsrc ON
                (debsrc.dist_id = dist_cpe.id)
            WHERE
                dist_cpe.cpe_vendor = 'sap'
                AND dist_cpe.cpe_product = 'gardenlinux'
                AND dist_cpe.deb_codename = :glVersion
            ORDER BY
                debsrc.deb_source""", nativeQuery = true)
    List<String> packagesForDistribution(@Param("glVersion") String glVersion);

    @Query(value = """
            SELECT
                all_cve.cve_id, deb_cve.deb_source, deb_cve.deb_version, deb_cve.deb_version_fixed, deb_cve.debsec_vulnerable
            FROM
                all_cve
                INNER JOIN deb_cve USING (cve_id)
                INNER JOIN dist_cpe ON (deb_cve.dist_id = dist_cpe.id)
            WHERE
                dist_cpe.cpe_product = 'gardenlinux'
                AND dist_cpe.deb_codename = '1592'
                AND deb_cve.deb_source = 'busybox'
                AND deb_cve.debsec_vulnerable = true
            ORDER BY
                all_cve.cve_id
            """, nativeQuery = true)
    List<String> packageWithVulnerabilities();
}
