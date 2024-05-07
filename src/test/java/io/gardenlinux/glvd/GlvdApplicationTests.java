package io.gardenlinux.glvd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class GlvdApplicationTests {

    static DockerImageName glvdPostgresImage = DockerImageName.parse("ghcr.io/gardenlinux/glvd-postgres:edgesampledata")
            .asCompatibleSubstituteFor("postgres");

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(glvdPostgresImage).withDatabaseName("glvd")
            .withUsername("glvd").withPassword("glvd");

    @Test
    void contextLoads() {
    }

}