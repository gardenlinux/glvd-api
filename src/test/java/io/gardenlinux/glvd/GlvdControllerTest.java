package io.gardenlinux.glvd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
class GlvdControllerTest {

    @Autowired
    private JsonMapper jsonMapper;

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
}
