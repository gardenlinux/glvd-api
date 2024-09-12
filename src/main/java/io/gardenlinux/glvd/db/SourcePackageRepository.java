package io.gardenlinux.glvd.db;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SourcePackageRepository extends JpaRepository<SourcePackage, String> {
    List<SourcePackage> findByGardenlinuxVersion(@Param("gardenlinux_version") String gardenlinux_version, Sort by);
}
