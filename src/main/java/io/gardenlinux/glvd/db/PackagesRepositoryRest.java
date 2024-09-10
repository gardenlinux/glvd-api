package io.gardenlinux.glvd.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "packages", path = "packages")
public interface PackagesRepositoryRest extends PagingAndSortingRepository<SourcePackageCve, String> {

    @RestResource(path = "package")
    List<SourcePackageCve> findBySourcePackageName(@Param("source_package_name") String source_package_name);
    @RestResource(path = "package_version")
    List<SourcePackageCve> findBySourcePackageNameAndSourcePackageVersion(@Param("source_package_name") String source_package_name, @Param("source_package_version") String source_package_version);
    @RestResource(path = "vuln")
    List<SourcePackageCve> findByCveIdAndGardenlinuxVersion(@Param("cve_id") String cve_id, @Param("gardenlinux_version") String gardenlinux_version);

    @RestResource(path = "cves")
    List<SourcePackageCve> findByGardenlinuxVersion(@Param("gardenlinux_version") String gardenlinux_version);
    @RestResource(path = "cves_package")
    List<SourcePackageCve> findBySourcePackageNameAndGardenlinuxVersion(@Param("source_package_name") String source_package_name, @Param("gardenlinux_version") String gardenlinux_version);

}
