package com.banking.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    public static final String CLIENT_IP_KEY_RESOLVER = "clientIpKeyResolver";

    @Bean(CLIENT_IP_KEY_RESOLVER)
    public KeyResolver clientIpKeyResolver() {
        return exchange -> {
            String forwardedFor = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-Forwarded-For");

            if (forwardedFor != null && !forwardedFor.isBlank()) {
                String clientIp = forwardedFor.split(",")[0].trim();
                return Mono.just(clientIp);
            }

            var remoteAddress = exchange.getRequest().getRemoteAddress();

            if (remoteAddress == null || remoteAddress.getAddress() == null) {
                return Mono.just("unknown-client");
            }

            return Mono.just(remoteAddress.getAddress().getHostAddress());
        };
    }
}