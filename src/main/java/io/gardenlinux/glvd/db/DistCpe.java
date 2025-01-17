package io.gardenlinux.glvd.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dist_cpe")
public class DistCpe {

    @Id
    @Column(nullable = false)
    private String id;

    @Column(name = "cpe_vendor", nullable = false)
    private String cpeVendor;

    @Column(name = "cpe_product", nullable = false)
    private String cpeProduct;

    @Column(name = "cpe_version", nullable = false)
    private String cpeVersion;

    @Column(name = "deb_codename", nullable = false)
    private String debCodename;

    public DistCpe() {
    }

    public DistCpe(String id, String cpeVendor, String cpeProduct, String cpeVersion, String debCodename) {
        this.id = id;
        this.cpeVendor = cpeVendor;
        this.cpeProduct = cpeProduct;
        this.cpeVersion = cpeVersion;
        this.debCodename = debCodename;
    }

    public String getId() {
        return id;
    }

    public String getCpeVendor() {
        return cpeVendor;
    }

    public String getCpeProduct() {
        return cpeProduct;
    }

    public String getCpeVersion() {
        return cpeVersion;
    }

    public String getDebCodename() {
        return debCodename;
    }
}


