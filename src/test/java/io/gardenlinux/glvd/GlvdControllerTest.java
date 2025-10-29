package io.gardenlinux.glvd;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
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
    public void shouldReturnCvesForGardenlinuxShouldNotReturnKernelCveMarkedAsResolved() {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/v1/cves/1592.5?sortBy=cveId&sortOrder=DESC")
                .then().statusCode(HttpStatus.SC_OK)
                .body("cveId", hasItems("CVE-2025-0938", "CVE-2025-21864", "CVE-2024-44953"))
                // CVE-2024-44953 is actually vulnerable, but marked as resolved via cve_context, thus it must return 'false' here
                .body("vulnerable", hasItems(true, true, false))
                .body("vulnStatus", hasItems("Received", "Analyzed", "Modified"));
    }

    @Test
    public void shouldReturnCvesForGardenlinuxWithRejectedCve() {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/v1/cves/1592.7?sortBy=cveId&sortOrder=DESC")
                .then().statusCode(HttpStatus.SC_OK)
                // purposefully does not contain CVE-2025-8197 because it is 'rejected'
                .body("size()", is(3))
                .body("cveId", hasItems("CVE-2024-35176", "CVE-2023-28755", "CVE-2024-44953"))
                .body("cveId", not(hasItems("CVE-2025-8197")))
                .body("vulnerable", hasItems(true, true, true))
                .body("vulnStatus", hasItems("Awaiting Analysis", "Modified", "Modified"));
    }

    @Test
    public void shuldReturnKernelCvesForGardenLinuxByPackageNameAndMarkResolvedCveAsNotVulnerable() {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/v1/cves/1592.5/packages/linux")
                .then().statusCode(HttpStatus.SC_OK)
                .body("cveId", hasItems("CVE-2025-21864", "CVE-2024-44953"))
                // CVE-2024-44953 is actually vulnerable, but marked as resolved via cve_context, thus it must return 'false' here
                .body("vulnerable", hasItems(true, false))
                .body("vulnStatus", hasItems("Analyzed", "Modified"));
    }

    @Test
    public void shouldReturnKernelCvesForGardenLinuxByPackageName() {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/v1/cves/1592.6/packages/linux")
                .then().statusCode(HttpStatus.SC_OK)
                .body("cveId", hasItems("CVE-2025-21864", "CVE-2024-44953"))
                // CVE-2024-44953 is vulnerable and not triaged for Garden Linux 1592.6
                .body("vulnerable", hasItems(true, true));
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
    public void reproduceIssue167WithPut() {
        var packageList = """
                  {
                  "packageNames": [
                    "libselinux",
                    "util-linux"
                  ]
                }""";

        given(this.spec).accept("application/json")
                .contentType("application/json")
                .body(packageList)
                .when().port(this.port).put("/v1/cves/1592.4/packages")
                .then().statusCode(HttpStatus.SC_OK)
                .body("size()", is(1))
                .body("sourcePackageName", hasItems("util-linux"))
                .body("cveId", hasItems("CVE-2022-0563"));
    }

    @Test
    public void reproduceIssue167WithGet() {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/v1/cves/1592.4/packages/util-linux,libselinux")
                .then().statusCode(HttpStatus.SC_OK)
                .body("size()", is(1))
                .body("sourcePackageName", hasItems("util-linux"))
                .body("cveId", hasItems("CVE-2022-0563"));
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
    public void shouldGetCveDetailsWithMultipleContexts() {
        given(this.spec).accept("application/json")
                .filter(document("getCveDetailsWithMultipleContexts",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cveDetails/CVE-2024-21626")
                .then().statusCode(200)
                .body("details.cveId", equalTo("CVE-2024-21626"))
                .body("contexts.description", hasItems("foo", "bar"));
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
                .body("details.kernelLtsVersion[0]", equalTo("6.6"))
                .body("details.kernelLtsVersion[1]", equalTo("6.12"))
                .body("details.isVulnerable", is(List.of(false, true, true, true, false, false, false, false)))
                .body("contexts.description", hasItems("Unit test for https://github.com/gardenlinux/glvd/issues/122"));
    }

    @Test
    public void shouldGetCveDetailsForKernelCve() {
        given(this.spec).accept("application/json")
                .filter(document("getCveDetailsKernel",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cveDetails/CVE-2024-53140")
                .then().statusCode(200)
                .body("details.cveId", equalTo("CVE-2024-53140"))
                .body("details.kernelLtsVersion[0]", equalTo("6.6"))
                .body("details.kernelLtsVersion[1]", equalTo("6.12"))
                .body("details.isVulnerable", is(List.of(false, false, false, false, false, false, false, false)))
                // has no cve context, assert it has no items in that list
                .body("contexts", empty());
    }

    @Test
    public void shouldGetCveDetailsWithContextsForKernelCveIsResolved() {
        given(this.spec).accept("application/json")
                .filter(document("getCveDetailsKernel",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cveDetails/CVE-2024-44953")
                .then().statusCode(200)
                .body("details.cveId", equalTo("CVE-2024-44953"))
                .body("details.kernelLtsVersion[0]", equalTo("6.6"))
                .body("details.kernelLtsVersion[1]", equalTo("6.12"))
                // This CVE is not fixed in any kernel, so all are vulnerable
                .body("details.isVulnerable", is(List.of(true, true, true, true, true, true, true)))
                // Is explicitly marked as "resolved"
                .body("contexts.resolved", hasItems(true));
    }

    @Test
    public void shouldGetCveDetailsForNonDebianCVE() {
        given(this.spec).accept("application/json")
                .filter(document("getCveDetailsNonDebian",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cveDetails/CVE-2024-7344")
                .then().statusCode(200)
                .body("details.cveId", equalTo("CVE-2024-7344"))
                .body("details.baseScoreV31", equalTo(8.2f))
                .body("details.vectorStringV31", equalTo("CVSS:3.1/AV:L/AC:L/PR:H/UI:N/S:C/C:H/I:H/A:H"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CVE-2024-7344", "CVE-2025-1419", "CVE-2004-0005", "CVE-2000-0258", "CVE-2000-0502", "CVE-2024-53564"})
    public void shouldGetCveDetailsForNonDebianCVEWithVariousCVESamples(String cveId) {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/v1/cveDetails/" + cveId)
                .then().statusCode(200)
                .body("details.cveId", equalTo(cveId));
    }

    @Test
    public void shouldGetCveDetailsForNonAvailableCveIdGracefully() {
        given(this.spec).accept("application/json")
                .filter(document("getCveDetailsNonDebian",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cveDetails/INVALID_CVE_ID")
                .then().statusCode(404)
                .header("Message", "INVALID_CVE_ID is not in the GLVD database. It might either be very new and not yet be available in GLVD, or the ID might be misspelled.");
    }

    @Test
    public void shouldGenerateReleaseNotesInformationWithThreeDigitVersion() {
        given(this.spec).accept("application/json")
                .filter(document("releaseNotes",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/releaseNotes/2000.1.0")
                .then().statusCode(200)
                .body("version", equalTo("2000.1.0"))
                .body("packageList.sourcePackageName", hasItems("util-linux"))
                .body("packageList.fixedCves", hasItems(List.of("CVE-2022-0563")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000.1.0", "1592.5", "1592.7", "1592.8", "1443.20"})
    public void shouldGeneratePatchReleaseNotesInformationAcceptedVersionNumbers(String version) {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/v1/releaseNotes/" + version)
                .then().statusCode(200)
                .body("version", equalTo(version));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2001", "1.2.3.4", "v0.0.1", "1592.11-mycoolfork", "2002-11", "trixie", "today"})
    public void shouldFailWithProperErrorMessageWhenInvalidVersionSchemaIsSpecified(String version) {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/v1/releaseNotes/" + version)
                .then().statusCode(400)
                .header("Message", "gardenlinuxVersion must be in n.n or n.n.n format, but was: " + version);
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
                .filter(document("patchReleaseNotesResolved",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/patchReleaseNotes/1592.7")
                .then().statusCode(200)
                .body("version", equalTo("1592.7"))
                .body("packageList.sourcePackageName", hasItems("linux"))
                .body("packageList.fixedCves", hasItems(List.of("CVE-2025-21864")));
    }

    @Test
    public void shouldGenerateEmptyPatchReleaseNotesForDistWithNoSourcePackages() {
        given(this.spec).accept("application/json")
                .filter(document("patchReleaseNotesEmpty",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/patchReleaseNotes/1592.8")
                .then().statusCode(200)
                .body("version", equalTo("1592.8"))
                .body("packageList", empty());
    }

    @Test
    public void reproduceIssue153() {
        // Reproducer for https://github.com/gardenlinux/glvd/issues/153
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/v1/patchReleaseNotes/1443.20")
                .then().statusCode(200)
                .body("version", equalTo("1443.20"))
                .body("packageList", empty());
    }

    @Test
    public void shouldReturnAllTriages() {
        given(this.spec).accept("application/json")
                .filter(document("triagesList",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/triage")
                .then().statusCode(200);
    }

    @Test
    public void shouldReportExpectedTriagesForGardenlinuxVersion() {
        given(this.spec).accept("application/json")
                .filter(document("triages",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/triage/1592.9")
                .then().statusCode(200)
                .body("cveId", hasItems("CVE-2005-2541", "CVE-2019-1010022"));
    }

    @Test
    public void shouldReportNoTriagesForGardenlinuxVersionWithoutCveContexts() {
        given(this.spec).accept("application/json")
                .filter(document("triagesEmpty",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/triage/1592.8")
                .then().statusCode(200)
                .body("", empty());
    }

    @Test
    public void shouldResolveGardenLinuxVersionToDistId() {
        given(this.spec).accept("text/plain")
                .when().port(this.port).get("/v1/distro/1592.10/distId")
                .then().statusCode(200)
                .body(equalTo("24"));
    }

    @Test
    public void shouldGetAllGardenLinuxVersions() {
        given(this.spec).accept("application/json")
                .filter(document("getAllGardenLinuxVersions",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/gardenlinuxVersions")
                .then().statusCode(200)
                .body("$", hasItems("1592.4", "1592.5", "1592.6", "1592.7", "1592.8", "1592.9", "1592.10", "today"));
    }

}
