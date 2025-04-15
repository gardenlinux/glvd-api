package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KernelCveDetailsRepository extends JpaRepository<KernelCveDetails, String> {
    Optional<KernelCveDetails> findByCveId(@Param("cve_id") String cveId);
}
