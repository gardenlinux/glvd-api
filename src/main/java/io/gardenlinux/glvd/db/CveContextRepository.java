package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CveContextRepository extends JpaRepository<CveContext, String> {
    List<CveContext> findByCveId(
            @Param("cve_id") String cve_id
    );
}
