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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class})
public class ActuatorEndpointTests {

    @LocalServerPort
    private Integer port;

    private RequestSpecification spec;

    @BeforeEach
    void setUp() {
        this.spec = new RequestSpecBuilder().build();

        RestAssured.baseURI = "http://localhost:" + port;
    }

    // We want to use actuator for k8s liveness and readiness probes
    @Test
    public void shouldReturnHealth() {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/actuator/health")
                .then().statusCode(HttpStatus.SC_OK)
                .body("status", is("UP"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"prometheus", "metrics", "env", "heapdump", "beans", "loggers", "mappings", "shutdown"})
    public void shouldNotReturnSensitiveEndpoints(String endpoint) {
        given(this.spec).accept("application/json")
                .when().port(this.port).get("/actuator/" + endpoint)
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body("path", is("/actuator/" + endpoint));
    }

}
