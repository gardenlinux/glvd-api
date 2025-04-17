package io.gardenlinux.glvd;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class GlvdControllerTest {

    @LocalServerPort
    private Integer port;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation)).build();

        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    public void shouldReturnCvesForGardenlinux() {
        given(this.spec).accept("application/json")
                .filter(document("getCveForDistro",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cves/1592.4?sortBy=cveId&sortOrder=DESC")
                .then().statusCode(HttpStatus.SC_OK)
                .body("sourcePackageName", hasItems("curl", "rsync", "jinja2", "python3.12", "linux"));
    }

    @Test
    public void shouldReturnCvesForListOfPackages() {
        given(this.spec).accept("application/json")
                .filter(document("getCveForPackages",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cves/1592.4/packages/jinja2,vim")
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void shouldReturnCvesForPutListOfPackages() {
        var packageList = """
                  {
                  "packageNames": [
                    "vim",
                    "bash",
                    "python3",
                    "curl",
                    "jinja2"
                  ]
                }""";

        given(this.spec).accept("application/json")
                .filter(document("getCveForPackagesPut",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .contentType("application/json")
                .body(packageList)
                .when().port(this.port).put("/v1/cves/1592.4/packages")
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void shouldGetPackagesForDistro() {
        given(this.spec).accept("application/json")
                .filter(document("getPackages",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/distro/1592.4")
                .then().statusCode(200)
                .body("sourcePackageName", hasItems("bind9", "dnsmasq", "jinja2", "systemd", "unbound"));
    }

    @Test
    public void shouldPackageWithVulnerabilities() {
        given(this.spec).accept("application/json")
                .filter(document("getPackageWithVulnerabilities",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/packages/jinja2")
                .then().statusCode(200)
                .body("sourcePackageName", hasItems("jinja2"))
                .body("cveId", hasItems("CVE-2024-56326"));
    }

    @Test
    public void shouldPackageWithVulnerabilitiesByVersion() {
        given(this.spec).accept("application/json")
                .filter(document("getPackageWithVulnerabilitiesByVersion",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/packages/jinja2/3.1.3-1")
                .then().statusCode(200)
                .body("sourcePackageName", hasItems("jinja2"));
    }

    @Test
    public void shouldGetPackagesByVulnerability() {
        given(this.spec).accept("application/json")
                .filter(document("getPackagesByVulnerability",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/distro/1592.4/CVE-2024-56326")
                .then().statusCode(200);
    }

    @Test
    public void shouldGetCveDetailsWithContexts() {
        given(this.spec).accept("application/json")
                .filter(document("getCveDetailsWithContexts",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cveDetails/CVE-2023-50387")
                .then().statusCode(200)
                .body("details.cveId", equalTo("CVE-2023-50387"))
                .body("contexts.description", hasItems("automated dummy data"));
    }

    @Test
    public void shouldGetCveDetailsWithContextsForKernelCve() {
        given(this.spec).accept("application/json")
                .filter(document("getCveDetailsWithContextsKernel",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cveDetails/CVE-2025-21864")
                .then().statusCode(200)
                .body("details.cveId", equalTo("CVE-2025-21864"))
                .body("contexts.description", hasItems("Unit test for https://github.com/gardenlinux/glvd/issues/122"));
    }

    @Test
    public void shouldGeneratePatchReleaseNotesInformation() {
        given(this.spec).accept("application/json")
                .filter(document("patchReleaseNotes",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/patchReleaseNotes/1592.5")
                .then().statusCode(200)
                .body("version", equalTo("1592.5"))
                .body("packageList.sourcePackageName", hasItems("jinja2", "rsync", "curl", "python3.12"))
                .body("packageList.fixedCves", hasItems(List.of("CVE-2024-56326"), List.of("CVE-2024-12085",
                        "CVE-2024-12086"), List.of("CVE-2024-11053"), List.of("CVE-2024-9287", "CVE-2025-0938")));
    }

    @Test
    public void shouldGeneratePatchReleaseNotesInformationWithKernelCveResolved() {
        given(this.spec).accept("application/json")
                .filter(document("patchReleaseNotes",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/patchReleaseNotes/1592.7")
                .then().statusCode(200)
                .body("version", equalTo("1592.7"))
                .body("packageList.sourcePackageName", hasItems("linux"))
                .body("packageList.fixedCves", hasItems(List.of("CVE-2025-21864")));
    }

}
