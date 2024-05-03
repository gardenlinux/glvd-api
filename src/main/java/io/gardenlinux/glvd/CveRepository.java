package io.gardenlinux.glvd;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CveRepository extends JpaRepository<Cve, String> {

}
