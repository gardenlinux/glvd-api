package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CveRepository extends JpaRepository<CveEntity, String> {

    @Query(value = """
             SELECT
                     all_cve.data  AS cveEntity
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
    List<String> cvesForDistribution(String vendor, String product, String codename);
}
