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
            .withUsername("glvd").withPassword("glvd");

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
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cves/1592.0?sortBy=cveId&sortOrder=DESC&pageNumber=4&pageSize=2")
				.then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void shouldReturnCvesForListOfPackages() {
        given(this.spec).accept("application/json")
                .filter(document("getCveForPackages",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cves/1592.0/packages/crun,vim?pageNumber=4&pageSize=2")
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void shouldGetPackagesForDistro() {
        given(this.spec).accept("application/json")
                .filter(document("getPackages",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/distro/1592.0?pageNumber=4&pageSize=2")
                .then().statusCode(200);
    }

    @Test
    public void shouldPackageWithVulnerabilities() {
        given(this.spec).accept("application/json")
                .filter(document("getPackageWithVulnerabilities",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/packages/vim?pageNumber=4&pageSize=2")
                .then().statusCode(200);
    }

    @Test
    public void shouldPackageWithVulnerabilitiesByVersion() {
        given(this.spec).accept("application/json")
                .filter(document("getPackageWithVulnerabilitiesByVersion",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/packages/vim/2:9.1.0496-1+b1?pageNumber=4&pageSize=2")
                .then().statusCode(200);
    }

    @Test
    public void shouldGetPackagesByVulnerability() {
        given(this.spec).accept("application/json")
                .filter(document("getPackagesByVulnerability",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/distro/1592.0/CVE-2023-50387")
                .then().statusCode(200).body("[0].cveId", equalTo("CVE-2023-50387"));
    }

}
