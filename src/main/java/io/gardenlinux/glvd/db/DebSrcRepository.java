package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DebSrcRepository extends JpaRepository<DebSrc, String> {
    List<DebSrc> findByDistId(@Param("dist_id") Integer dist_id);
}
