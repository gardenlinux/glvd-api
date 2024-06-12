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
                     dist_cpe.cpe_vendor = ?1 AND
                     dist_cpe.cpe_product = ?2 and
                     dist_cpe.deb_codename = ?3
                 ORDER BY
                     all_cve.cve_id
            """, nativeQuery = true)
    List<CveEntity> cvesForDistribution(String vendor, String product, String codename);

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
                 dist_cpe.cpe_vendor = ?1 AND
                 dist_cpe.cpe_product = ?2 AND
                 dist_cpe.deb_codename = ?3 AND
                 deb_cve.deb_source = ANY(?4 ::TEXT[])
            ORDER BY
                all_cve.cve_id
            """, nativeQuery = true)
    List<String> cvesForPackageList(String vendor, String product, String codename, String packages);

}
