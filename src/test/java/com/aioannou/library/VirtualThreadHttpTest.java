package com.aioannou.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(VirtualThreadHttpTest.ThreadProbeConfiguration.class)
class VirtualThreadHttpTest {
    @Autowired
    private Environment environment;

    @Test
    void embeddedServerHandlesRequestsOnVirtualThreads() throws Exception {
        var uri = URI.create("http://localhost:" + environment.getRequiredProperty("local.server.port")
            + "/v3/api-docs");
        try (var client = HttpClient.newHttpClient()) {
            var response = client.send(HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(10)).build(),
                HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertEquals("true", response.headers().firstValue("X-Test-Virtual-Thread").orElseThrow());
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ThreadProbeConfiguration {
        @Bean
        Filter virtualThreadProbe() {
            return (request, response, chain) -> {
                ((HttpServletResponse) response).setHeader("X-Test-Virtual-Thread",
                    Boolean.toString(Thread.currentThread().isVirtual()));
                chain.doFilter(request, response);
            };
        }
    }
}
