package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface NvdCveRepository extends JpaRepository<NvdCve, String> {
    NvdCve findByCveId(
            @Param("cve_id") String cve_id
    );
}
