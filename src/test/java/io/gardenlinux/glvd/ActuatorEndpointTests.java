package io.gardenlinux.glvd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ActuatorEndpointTests {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

    // We want to use actuator for k8s liveness and readiness probes
	@Test
	void shouldReturnHealth() throws Exception {
		this.mockMvc.perform(get("/actuator/health"))
			.andExpect(status().isOk())
            .andExpect(jsonPath("status", is("UP")));
	}

    @ParameterizedTest
    @ValueSource(strings = {"prometheus", "metrics", "env", "heapdump", "beans", "loggers", "mappings", "shutdown"})
    public void shouldNotReturnSensitiveEndpoints(String endpoint) throws Exception {
        this.mockMvc.perform(get("/actuator/" + endpoint))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("No static resource actuator/" + endpoint + "."));
    }

}
