package io.gardenlinux.glvd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
class GlvdControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    static ResultHandler doc(String identifier) {
        return document(
                identifier,
                preprocessRequest(modifyUris().host("security.gardenlinux.org").removePort()),
                preprocessResponse(prettyPrint())
        );
    }

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(restDocumentation).snippets().withAdditionalDefaults()).build();
    }

    @Test
    void shouldReturnCvesForGardenlinuxImage() throws Exception {
        this.mockMvc.perform(get("/v1/cves/1592.4/image/azure-gardener_prod"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sourcePackageName", hasItems("curl", "rsync", "jinja2", "python3.12", "linux")))
                .andDo(doc("getCveForImage"));
    }

    @Test
    void shouldReturnCvesForGardenlinux() throws Exception {
        this.mockMvc.perform(get("/v1/cves/1592.4?sortBy=cveId&sortOrder=DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sourcePackageName", hasItems("curl", "rsync", "jinja2", "python3.12", "linux")))
                .andDo(doc("getCveForDistro"));
    }

    @Test
    void shouldReturnCvesForGardenlinuxShouldNotReturnKernelCveMarkedAsResolved() throws Exception {
        this.mockMvc.perform(get("/v1/cves/1592.5?sortBy=cveId&sortOrder=DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].cveId", contains("CVE-2025-0938", "CVE-2025-21864", "CVE-2024-44953")))
                // CVE-2024-44953 is actually vulnerable, but marked as resolved via cve_context, thus it must return 'false' here
                .andExpect(jsonPath("$[*].vulnerable", contains(false, true, false)))
                .andExpect(jsonPath("$[*].vulnStatus", contains("Received", "Analyzed", "Modified")));
    }

    @Test
    void shouldReturnCvesForGardenlinuxWithRejectedCve() throws Exception {
        this.mockMvc.perform(get("/v1/cves/1592.7?sortBy=cveId&sortOrder=DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].cveId", hasItems("CVE-2024-35176", "CVE-2023-28755", "CVE-2024-44953")))
                .andExpect(jsonPath("$[*].cveId", not(hasItem("CVE-2025-8197"))))
                .andExpect(jsonPath("$[*].vulnerable", hasItems(true, true, true)))
                .andExpect(jsonPath("$[*].vulnStatus", hasItems("Awaiting Analysis", "Modified", "Modified")))
                .andDo(doc("getCveForDistroWithRejectedCve"));
    }

    @Test
    void shouldReturnKernelCvesForGardenLinuxByPackageNameAndMarkResolvedCveAsNotVulnerable() throws Exception {
        this.mockMvc.perform(get("/v1/cves/1592.5/packages/linux"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].cveId", hasItems("CVE-2025-21864", "CVE-2024-44953")))
                .andExpect(jsonPath("$[*].vulnerable", hasItems(true, false)))
                .andExpect(jsonPath("$[*].vulnStatus", hasItems("Analyzed", "Modified")))
                .andDo(doc("getKernelCvesForGardenLinuxByPackageName"));
    }

    @Test
    void shouldReturnKernelCvesForGardenLinuxByPackageName() throws Exception {
        this.mockMvc.perform(get("/v1/cves/1592.6/packages/linux"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].cveId", hasItems("CVE-2025-21864", "CVE-2024-44953")))
                .andExpect(jsonPath("$[*].vulnerable", hasItems(true, true)))
                .andDo(doc("getKernelCvesForGardenLinuxByPackageName"));
    }

    @Test
    void shouldReturnCvesForListOfPackages() throws Exception {
        this.mockMvc.perform(get("/v1/cves/1592.4/packages/jinja2,vim"))
                .andExpect(status().isOk())
                .andDo(doc("getCveForPackages"));
    }

    @Test
    void shouldReturnCvesForPutListOfPackages() throws Exception {
        String packageList = """
                  {
                    "packageNames": [
                      "vim",
                      "bash",
                      "python3",
                      "curl",
                      "jinja2"
                    ]
                  }
                """;
        this.mockMvc.perform(
                        put("/v1/cves/1592.4/packages")
                                .contentType("application/json")
                                .content(packageList)
                )
                .andExpect(status().isOk())
                .andDo(doc("getCveForPackagesPut"));
    }

    @Test
    void reproduceIssue167WithPut() throws Exception {
        String packageList = """
                  {
                    "packageNames": [
                      "libselinux",
                      "util-linux"
                    ]
                  }
                """;
        this.mockMvc.perform(
                        put("/v1/cves/1592.4/packages")
                                .contentType("application/json")
                                .content(packageList)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].sourcePackageName", hasItem("util-linux")))
                .andExpect(jsonPath("$[*].cveId", hasItem("CVE-2022-0563")));
    }

    @Test
    void reproduceIssue167WithGet() throws Exception {
        this.mockMvc.perform(get("/v1/cves/1592.4/packages/util-linux,libselinux"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].sourcePackageName", hasItem("util-linux")))
                .andExpect(jsonPath("$[*].cveId", hasItem("CVE-2022-0563")));
    }

    @Test
    void shouldGetPackagesForDistro() throws Exception {
        this.mockMvc.perform(get("/v1/distro/1592.4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sourcePackageName", hasItems("bind9", "dnsmasq", "jinja2", "systemd", "unbound")))
                .andDo(doc("getPackages"));
    }

    @Test
    void shouldPackageWithVulnerabilities() throws Exception {
        this.mockMvc.perform(get("/v1/packages/jinja2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sourcePackageName", hasItem("jinja2")))
                .andExpect(jsonPath("$[*].cveId", hasItem("CVE-2024-56326")))
                .andDo(doc("getPackageWithVulnerabilities"));
    }

    @Test
    void shouldPackageWithVulnerabilitiesByVersion() throws Exception {
        this.mockMvc.perform(get("/v1/packages/jinja2/3.1.3-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sourcePackageName", hasItem("jinja2")))
                .andDo(doc("getPackageWithVulnerabilitiesByVersion"));
    }

    @Test
    void shouldGetPackagesByVulnerability() throws Exception {
        this.mockMvc.perform(get("/v1/distro/1592.4/CVE-2024-56326"))
                .andExpect(status().isOk())
                .andDo(doc("getPackagesByVulnerability"));
    }

    @Test
    void shouldGetCveDetailsWithContexts() throws Exception {
        this.mockMvc.perform(get("/v1/cveDetails/CVE-2023-50387"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.cveId", is("CVE-2023-50387")))
                .andExpect(jsonPath("$.contexts[0].description", is("automated dummy data")))
                .andDo(doc("getCveDetailsWithContexts"));
    }

    @Test
    void shouldGetCveDetailsWithMultipleContexts() throws Exception {
        this.mockMvc.perform(get("/v1/cveDetails/CVE-2024-21626"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.cveId", is("CVE-2024-21626")))
                .andExpect(jsonPath("$.contexts[0].description", is("foo")))
                .andExpect(jsonPath("$.contexts[1].description", is("bar")))
                .andDo(doc("getCveDetailsWithMultipleContexts"));
    }

    @Test
    void shouldGetCveDetailsWithContextsForKernelCve() throws Exception {
        this.mockMvc.perform(get("/v1/cveDetails/CVE-2025-21864"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.cveId", is("CVE-2025-21864")))
                .andExpect(jsonPath("$.details.kernelLtsVersion[0]", is("6.6")))
                .andExpect(jsonPath("$.details.kernelLtsVersion[1]", is("6.12")))
                .andExpect(jsonPath("$.details.isVulnerable", contains(false, true, true, true, false, false, false, false)))
                .andExpect(jsonPath("$.contexts[0].description", is("Unit test for https://github.com/gardenlinux/glvd/issues/122")))
                .andDo(doc("getCveDetailsWithContextsKernel"));
    }

    @Test
    void shouldGetCveDetailsForKernelCve() throws Exception {
        this.mockMvc.perform(get("/v1/cveDetails/CVE-2024-53140"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.cveId", is("CVE-2024-53140")))
                .andExpect(jsonPath("$.details.kernelLtsVersion[0]", is("6.6")))
                .andExpect(jsonPath("$.details.kernelLtsVersion[1]", is("6.12")))
                .andExpect(jsonPath("$.details.isVulnerable", contains(false, false, false, false, false, false, false, false)))
                .andExpect(jsonPath("$.contexts", empty()))
                .andDo(doc("getCveDetailsKernel"));
    }

    @Test
    void shouldGetCveDetailsWithContextsForKernelCveIsResolved() throws Exception {
        this.mockMvc.perform(get("/v1/cveDetails/CVE-2024-44953"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.cveId", is("CVE-2024-44953")))
                .andExpect(jsonPath("$.details.kernelLtsVersion[0]", is("6.6")))
                .andExpect(jsonPath("$.details.kernelLtsVersion[1]", is("6.12")))
                .andExpect(jsonPath("$.details.isVulnerable", contains(true, true, true, true, true, true, true)))
                .andExpect(jsonPath("$.contexts[0].resolved", is(true)))
                .andDo(doc("getCveDetailsKernel"));
    }

    @Test
    void shouldGetCveDetailsForNonDebianCVE() throws Exception {
        this.mockMvc.perform(get("/v1/cveDetails/CVE-2024-7344"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.cveId", is("CVE-2024-7344")))
                .andExpect(jsonPath("$.details.baseScoreV31", is(8.2)))
                .andExpect(jsonPath("$.details.vectorStringV31", is("CVSS:3.1/AV:L/AC:L/PR:H/UI:N/S:C/C:H/I:H/A:H")))
                .andDo(doc("getCveDetailsNonDebian"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CVE-2024-7344", "CVE-2025-1419", "CVE-2004-0005", "CVE-2000-0258", "CVE-2000-0502", "CVE-2024-53564"})
    void shouldGetCveDetailsForNonDebianCVEWithVariousCVESamples(String cveId) throws Exception {
        this.mockMvc.perform(get("/v1/cveDetails/" + cveId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.cveId", is(cveId)));
    }

    @Test
    void shouldGetCveDetailsForNonAvailableCveIdGracefully() throws Exception {
        this.mockMvc.perform(get("/v1/cveDetails/INVALID_CVE_ID"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Message", containsString("INVALID_CVE_ID is not in the GLVD database. It might either be very new and not yet be available in GLVD, or the ID might be misspelled.")))
                .andDo(doc("getCveDetailsNonDebian"));
    }

    @Test
    void shouldGenerateReleaseNotesInformationWithThreeDigitVersion() throws Exception {
        this.mockMvc.perform(get("/v1/releaseNotes/2000.1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is("2000.1.0")))
                .andExpect(jsonPath("$.packageList[0].sourcePackageName", is("util-linux")))
                .andExpect(jsonPath("$.packageList[0].fixedCves", hasItems("CVE-2022-0563")))
                .andDo(doc("releaseNotes"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000.1.0", "1592.5", "1592.7", "1592.8", "1443.20"})
    void shouldGeneratePatchReleaseNotesInformationAcceptedVersionNumbers(String version) throws Exception {
        this.mockMvc.perform(get("/v1/releaseNotes/" + version))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is(version)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2001", "1.2.3.4", "v0.0.1", "1592.11-mycoolfork", "2002-11", "trixie", "today"})
    void shouldFailWithProperErrorMessageWhenInvalidVersionSchemaIsSpecified(String version) throws Exception {
        this.mockMvc.perform(get("/v1/releaseNotes/" + version))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Message", containsString("gardenlinuxVersion must be in n.n or n.n.n format, but was: " + version)));
    }

    @Test
    void shouldGeneratePatchReleaseNotesInformation() throws Exception {
        this.mockMvc.perform(get("/v1/patchReleaseNotes/1592.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is("1592.5")))
                .andExpect(jsonPath("$.packageList[0].sourcePackageName", is(oneOf("jinja2", "rsync", "curl", "python3.12"))))
                .andExpect(jsonPath("$.packageList[*].fixedCves[*]", hasItems(
                        "CVE-2024-56326",
                        "CVE-2024-12085",
                        "CVE-2024-12086",
                        "CVE-2024-11053",
                        "CVE-2024-9287",
                        "CVE-2025-0938"
                )))
                .andDo(doc("patchReleaseNotes"));
    }

    @Test
    void shouldGeneratePatchReleaseNotesInformationWithKernelCveResolved() throws Exception {
        this.mockMvc.perform(get("/v1/patchReleaseNotes/1592.7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is("1592.7")))
                .andExpect(jsonPath("$.packageList[0].sourcePackageName", is("linux")))
                .andExpect(jsonPath("$.packageList[0].fixedCves", contains("CVE-2025-21864")))
                .andDo(doc("patchReleaseNotesResolved"));
    }

    @Test
    void shouldGenerateEmptyPatchReleaseNotesForDistWithNoSourcePackages() throws Exception {
        this.mockMvc.perform(get("/v1/patchReleaseNotes/1592.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is("1592.8")))
                .andExpect(jsonPath("$.packageList", empty()))
                .andDo(doc("patchReleaseNotesEmpty"));
    }

    @Test
    void reproduceIssue153() throws Exception {
        this.mockMvc.perform(get("/v1/patchReleaseNotes/1443.20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version", is("1443.20")))
                .andExpect(jsonPath("$.packageList", empty()));
    }

    @Test
    void shouldReturnAllTriages() throws Exception {
        this.mockMvc.perform(get("/v1/triage"))
                .andExpect(status().isOk())
                .andDo(doc("triagesList"));
    }

    @Test
    void shouldReportExpectedTriagesForGardenlinuxVersion() throws Exception {
        this.mockMvc.perform(get("/v1/triage/gardenlinux/1592.9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].cveId", hasItems("CVE-2005-2541", "CVE-2019-1010022")))
                .andDo(doc("triagesGardenlinux"));
    }

    @Test
    void shouldReportExpectedTriagesForCveId() throws Exception {
        this.mockMvc.perform(get("/v1/triage/cve/CVE-2019-1010022"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].cveId", hasItem("CVE-2019-1010022")))
                .andDo(doc("triagesCve"));
    }

    @Test
    void shouldReportExpectedTriagesForPackage() throws Exception {
        this.mockMvc.perform(get("/v1/triage/sourcePackage/glibc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].cveId", hasItem("CVE-2019-1010022")))
                .andDo(doc("triagesPackage"));
    }

    @Test
    void shouldReportNoTriagesForGardenlinuxVersionWithoutCveContexts() throws Exception {
        this.mockMvc.perform(get("/v1/triage/gardenlinux/1592.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()))
                .andDo(doc("triagesEmpty"));
    }

    @Test
    void shouldResolveGardenLinuxVersionToDistId() throws Exception {
        this.mockMvc.perform(get("/v1/distro/1592.10/distId").accept("text/plain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(24)));
    }

    @Test
    void shouldGetAllGardenLinuxVersions() throws Exception {
        this.mockMvc.perform(get("/v1/gardenlinuxVersions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems("1592.4", "1592.5", "1592.6", "1592.7", "1592.8", "1592.9", "1592.10", "today")))
                .andDo(doc("getAllGardenLinuxVersions"));
    }
}
