package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "sourcepackagecve", path = "sourcepackagecve")
public interface PackagesRepositoryRest extends PagingAndSortingRepository<SourcePackageCve, String>, CrudRepository<SourcePackageCve, String> {

    List<SourcePackageCve> findByDebSource(@Param("deb_source") String deb_source);
}
