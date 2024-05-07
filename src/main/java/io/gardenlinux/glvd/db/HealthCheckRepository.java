package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HealthCheckRepository extends JpaRepository<HealthCheckEntity, String> {

    @Query(value = "SELECT TRUE", nativeQuery = true)
    String checkDbConnection();

}
