package com.banking.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void shouldGenerateCorrelationIdWhenMissing() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").build()
        );

        WebFilterChain chain = ex -> Mono.empty();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getHeaders().getFirst(CorrelationIdFilter.HEADER_NAME))
                .isNotBlank();
    }

    @Test
    void shouldKeepExistingCorrelationId() {
        String correlationId = "test-correlation-id";

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test")
                        .header(CorrelationIdFilter.HEADER_NAME, correlationId)
                        .build()
        );

        WebFilterChain chain = ex -> Mono.empty();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getHeaders().getFirst(CorrelationIdFilter.HEADER_NAME))
                .isEqualTo(correlationId);
    }
}