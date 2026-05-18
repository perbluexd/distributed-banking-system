package com.banking.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "management.health.redis.enabled=false",
                "management.endpoint.health.show-details=never"
        }
)
@AutoConfigureWebTestClient(timeout = "60s")
class SecurityConfigTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("auth.jwt.secret", () -> "cambia-esto-por-una-clave-larga-de-minimo-32-caracteres-123456");
        registry.add("auth.jwt.issuer", () -> "auth-service");
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void actuatorHealthShouldBePublic() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void accountsShouldBeProtected() {
        webTestClient.get()
                .uri("/accounts/test")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
}