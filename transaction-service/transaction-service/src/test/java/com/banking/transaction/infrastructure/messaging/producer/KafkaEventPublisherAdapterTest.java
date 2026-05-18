package com.banking.transaction.infrastructure.messaging.producer;

import com.banking.transaction.domain.model.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.Mockito.*;

class KafkaEventPublisherAdapterTest {

    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final KafkaEventPublisherAdapter adapter =
            new KafkaEventPublisherAdapter(kafkaTemplate, objectMapper);

    @Test
    void shouldPublishTransferCreatedOutboxEvent() {
        UUID transferId = UUID.randomUUID();

        String payload = """
                {
                  "transferId": "%s",
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": 100.00,
                  "currency": "PEN",
                  "status": "PENDING",
                  "occurredAt": "2026-01-01T00:00:00Z"
                }
                """.formatted(
                transferId,
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        OutboxEvent event = OutboxEvent.create(
                "Transfer",
                transferId,
                "TransferCreatedEvent",
                "transfer.created",
                transferId.toString(),
                payload
        );

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(mock(java.util.concurrent.CompletableFuture.class));

        adapter.publish(event);

        verify(kafkaTemplate).send(
                eq("transfer.created"),
                eq(transferId.toString()),
                any()
        );
    }
}