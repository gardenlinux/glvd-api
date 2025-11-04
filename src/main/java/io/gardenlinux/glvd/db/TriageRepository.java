package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TriageRepository extends JpaRepository<Triage, String> {
    List<Triage> findByTriageGardenLinuxVersion(
            @Param("triage_gardenlinux_version") String triage_gardenlinux_version
    );
}
