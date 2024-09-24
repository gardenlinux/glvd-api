package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CveDetailsRepository extends JpaRepository<CveDetails, String> {
    CveDetails findByCveId(
            @Param("cve_id") String cve_id
    );
}
