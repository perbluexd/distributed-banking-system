package com.banking.gateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.banking.gateway.config.RateLimitConfig.CLIENT_IP_KEY_RESOLVER;

@Configuration
public class RouteConfig {

    private final KeyResolver clientIpKeyResolver;

    public RouteConfig(
            @Qualifier(CLIENT_IP_KEY_RESOLVER) KeyResolver clientIpKeyResolver
    ) {
        this.clientIpKeyResolver = clientIpKeyResolver;
    }

    @Bean
    @Primary
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(20, 40, 1);
    }

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder,
            RedisRateLimiter redisRateLimiter
    ) {
        return builder.routes()

                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f.requestRateLimiter(config -> config
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(clientIpKeyResolver)
                        ))
                        .uri("http://auth-service:8080"))

                .route("customer-service", r -> r
                        .path("/customers/**")
                        .filters(f -> f.requestRateLimiter(config -> config
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(clientIpKeyResolver)
                        ))
                        .uri("http://customer-service:8081"))

                .route("account-service", r -> r
                        .path("/accounts/**")
                        .filters(f -> f.requestRateLimiter(config -> config
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(clientIpKeyResolver)
                        ))
                        .uri("http://account-service:8083"))

                .route("transaction-service", r -> r
                        .path("/transfers/**")
                        .filters(f -> f.requestRateLimiter(config -> config
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(clientIpKeyResolver)
                        ))
                        .uri("http://transaction-service:8084"))

                .route("notification-service", r -> r
                        .path("/notifications/**")
                        .filters(f -> f.requestRateLimiter(config -> config
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(clientIpKeyResolver)
                        ))
                        .uri("http://notification-service:8085"))

                .route("audit-service", r -> r
                        .path("/audit-records/**")
                        .filters(f -> f.requestRateLimiter(config -> config
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(clientIpKeyResolver)
                        ))
                        .uri("http://audit-service:8086"))

                .build();
    }
}