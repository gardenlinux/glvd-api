package io.gardenlinux.glvd.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TriageRepository extends JpaRepository<Triage, String> {
    List<Triage> findByTriageGardenLinuxVersion(
            @Param("triage_gardenlinux_version") String triage_gardenlinux_version
    );

    List<Triage> findByCveId(
            @Param("cve_id") String cve_id
    );

    List<Triage> findBySourcePackageName(
            @Param("source_package_name") String source_package_name
    );

    Page<Triage> findAll(Pageable pageable);
}
