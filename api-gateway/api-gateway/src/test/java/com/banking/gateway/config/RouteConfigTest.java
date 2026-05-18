package com.banking.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class RouteConfigTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("auth.jwt.secret", () -> "cambia-esto-por-una-clave-larga-de-minimo-32-caracteres-123456");
        registry.add("auth.jwt.issuer", () -> "auth-service");
    }

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void shouldRegisterGatewayRoutes() {
        Set<String> routeIds = routeLocator.getRoutes()
                .map(route -> route.getId())
                .collect(Collectors.toSet())
                .block();

        assertThat(routeIds).contains(
                "auth-service",
                "customer-service",
                "account-service",
                "transaction-service",
                "notification-service",
                "audit-service"
        );
    }
}