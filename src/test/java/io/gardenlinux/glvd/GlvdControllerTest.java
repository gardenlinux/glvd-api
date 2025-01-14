package io.gardenlinux.glvd;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class GlvdControllerTest {

    static DockerImageName glvdPostgresImage = DockerImageName
            .parse(TestConfig.DbContainerImage)
            .asCompatibleSubstituteFor("postgres");

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(glvdPostgresImage)
            .withDatabaseName("glvd")
            .withUsername("glvd")
            .withPassword("glvd")
            .withInitScripts("test-data/01-schema.sql", "test-data/02-sample-data.sql");

    @LocalServerPort
    private Integer port;

    private RequestSpecification spec;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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
				.then().statusCode(HttpStatus.SC_OK);
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
                .then().statusCode(200);
    }

    @Test
    public void shouldPackageWithVulnerabilities() {
        given(this.spec).accept("application/json")
                .filter(document("getPackageWithVulnerabilities",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/packages/jinja2")
                .then().statusCode(200);
    }

    @Test
    public void shouldPackageWithVulnerabilitiesByVersion() {
        given(this.spec).accept("application/json")
                .filter(document("getPackageWithVulnerabilitiesByVersion",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.ingress.glvd.gardnlinux.shoot.canary.k8s-hana.ondemand.com").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/packages/jinja2/3.1.3-1")
                .then().statusCode(200);
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
                .then().statusCode(200);
    }

}
