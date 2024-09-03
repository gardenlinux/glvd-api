package io.gardenlinux.glvd;

import io.gardenlinux.glvd.db.CveRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.hamcrest.Matchers.containsString;
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

    @Autowired
    CveRepository cveRepository;

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
    public void shouldGetCveById() {
        given(this.spec).accept("application/json")
                .filter(document("getCve",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cves/CVE-2024-1549")
				.then().statusCode(HttpStatus.SC_OK).body("id", containsString("CVE-2024-1549"));
    }

    @Test
    void tryGetNonExistingCveById() {
        given().contentType(ContentType.JSON)
				.when().get("/v1/cves/CVE-1989-1234")
				.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void shouldReturnCvesForBookworm() {
        given(this.spec).accept("application/json")
                .filter(document("getCveForDistro",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cves/gardenlinux/1592")
				.then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void shouldReturnCvesForBookwormByVersion() {
        given(this.spec).accept("application/json")
                .filter(document("getCveForDistroByVersion",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cves/gardenlinux/version/1592.0")
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void shouldReturnCvesForListOfPackages() {
        given(this.spec).accept("application/json")
                .filter(document("getCveForPackages",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cves/gardenlinux/1592/packages/crun,vim")
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void shouldReturnCvesForListOfPackagesByDistroVersion() {
        given(this.spec).accept("application/json")
                .filter(document("getCveForPackagesByDistroVersion",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/cves/gardenlinux/version/1592.0/packages/crun,vim")
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void shouldBeReady() {
        given(this.spec)
                .filter(document("readiness",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/readiness")
				.then().statusCode(HttpStatus.SC_OK).body("dbCheck", containsString("true"));
    }

    @Test
    public void shouldGetPackagesForDistro() {
        given(this.spec).accept("application/json")
                .filter(document("getPackages",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/packages/distro/gardenlinux/1592.0")
                .then().statusCode(200);
    }

    @Test
    public void shouldPackageWithVulnerabilities() {
        given(this.spec).accept("application/json")
                .filter(document("getPackageWithVulnerabilities",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/packages/vim")
                .then().statusCode(200);
    }

    @Test
    public void shouldPackageWithVulnerabilitiesByVersion() {
        given(this.spec).accept("application/json")
                .filter(document("getPackageWithVulnerabilitiesByVersion",
                        preprocessRequest(modifyUris().scheme("https").host("glvd.gardenlinux.io").removePort()),
                        preprocessResponse(prettyPrint())))
                .when().port(this.port).get("/v1/packages/vim/2:9.1.0496-1+b1")
                .then().statusCode(200);
    }

}