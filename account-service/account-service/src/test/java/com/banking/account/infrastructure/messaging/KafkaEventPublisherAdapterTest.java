package com.banking.account.infrastructure.messaging;

import com.banking.account.domain.model.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaEventPublisherAdapterTest {

    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final KafkaEventPublisherAdapter adapter =
            new KafkaEventPublisherAdapter(kafkaTemplate, objectMapper);

    @Test
    void shouldPublishOutboxEvent() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        String payload = """
                {
                  "accountId": "%s",
                  "customerId": "%s",
                  "accountNumber": "ACC-123456789",
                  "accountType": "SAVINGS",
                  "balance": 0.00,
                  "currency": "PEN",
                  "occurredAt": "2026-05-05T10:15:30Z"
                }
                """.formatted(accountId, customerId);

        OutboxEvent event = OutboxEvent.create(
                "Account",
                accountId,
                "AccountCreatedEvent",
                "account.created",
                "key-123",
                payload
        );

        SendResult<String, Object> sendResult = mock(SendResult.class);

        when(kafkaTemplate.send(
                eq("account.created"),
                eq("key-123"),
                any(Object.class)
        )).thenReturn(CompletableFuture.completedFuture(sendResult));

        adapter.publish(event);

        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(
                eq("account.created"),
                eq("key-123"),
                payloadCaptor.capture()
        );

        assertNotNull(payloadCaptor.getValue());
    }
}