package com.banking.gateway.filter;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements WebFilter {

    public static final String HEADER_NAME = "X-Correlation-Id";

    @Override
    @NonNull
    public Mono<Void> filter(
            @NonNull ServerWebExchange exchange,
            @NonNull WebFilterChain chain
    ) {
        String incomingCorrelationId = exchange.getRequest()
                .getHeaders()
                .getFirst(HEADER_NAME);

        final String correlationId = (incomingCorrelationId == null || incomingCorrelationId.isBlank())
                ? UUID.randomUUID().toString()
                : incomingCorrelationId;

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.header(HEADER_NAME, correlationId))
                .build();

        mutatedExchange.getResponse()
                .getHeaders()
                .set(HEADER_NAME, correlationId);

        return chain.filter(mutatedExchange);
    }
}