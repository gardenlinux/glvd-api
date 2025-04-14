package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KernelCveRepository extends JpaRepository<KernelCve, String> {
}
