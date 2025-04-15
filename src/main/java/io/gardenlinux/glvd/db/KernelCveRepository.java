package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KernelCveRepository extends JpaRepository<KernelCve, String> {
    List<KernelCve> findByLtsVersion(@Param("lts_version") String ltsVersion);
    List<KernelCve> findByGardenlinuxVersion(@Param("gardenlinux_version") String gardenlinuxVersion);
}
