package io.gardenlinux.glvd.db;

import io.gardenlinux.glvd.dto.Cve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CveRepository extends JpaRepository<CveEntity, String> {

    @Query(value = """
             SELECT
                     all_cve.*
                 FROM
                     all_cve
                     INNER JOIN deb_cve USING (cve_id)
                     INNER JOIN dist_cpe ON (deb_cve.dist_id = dist_cpe.id)
                 WHERE
                     dist_cpe.cpe_product = ?1 and
                     dist_cpe.deb_codename = ?2
                 ORDER BY
                     all_cve.cve_id
            """, nativeQuery = true)
    List<CveEntity> cvesForDistribution(String product, String codename);

    @Query(value = """
             SELECT
                     all_cve.*
                 FROM
                     all_cve
                     INNER JOIN deb_cve USING (cve_id)
                     INNER JOIN dist_cpe ON (deb_cve.dist_id = dist_cpe.id)
                 WHERE
                     dist_cpe.cpe_product = ?1 and
                     dist_cpe.cpe_version = ?2
                 ORDER BY
                     all_cve.cve_id
            """, nativeQuery = true)
    List<CveEntity> cvesForDistributionVersion(String product, String version);

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
                 dist_cpe.cpe_product = ?1 AND
                 dist_cpe.deb_codename = ?2 AND
                 deb_cve.deb_source = ANY(?3 ::TEXT[])
            ORDER BY
                all_cve.cve_id
            """, nativeQuery = true)
    List<String> cvesForPackageList(String product, String codename, String packages);

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
                 dist_cpe.cpe_product = ?1 AND
                 dist_cpe.cpe_version = ?2 AND
                 deb_cve.deb_source = ANY(?3 ::TEXT[])
            ORDER BY
                all_cve.cve_id
            """, nativeQuery = true)
    List<String> cvesForPackageListVersion(String product, String version, String packages);

}
