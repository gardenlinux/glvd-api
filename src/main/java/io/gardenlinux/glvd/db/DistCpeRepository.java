package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DistCpeRepository extends JpaRepository<DistCpe, String> {
    DistCpe getByCpeVersion(@Param("cpe_version") String cpe_version);
}
