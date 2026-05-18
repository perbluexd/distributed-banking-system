package com.banking.transaction.infrastructure.messaging.producer;

import com.banking.transaction.application.port.out.EventPublisherPort;
import com.banking.transaction.domain.model.OutboxEvent;
import com.banking.transaction.infrastructure.messaging.event.TransferCompletedEvent;
import com.banking.transaction.infrastructure.messaging.event.TransferCreatedEvent;
import com.banking.transaction.infrastructure.messaging.event.TransferFailedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventPublisherAdapter(
            KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OutboxEvent event) {
        Object payload = deserializePayload(event);

        kafkaTemplate.send(
                event.getTopic(),
                event.getEventKey(),
                payload
        ).join();
    }

    private Object deserializePayload(OutboxEvent event) {
        try {
            return switch (event.getEventType()) {
                case "TransferCreatedEvent" ->
                        objectMapper.readValue(event.getPayload(), TransferCreatedEvent.class);

                case "TransferCompletedEvent" ->
                        objectMapper.readValue(event.getPayload(), TransferCompletedEvent.class);

                case "TransferFailedEvent" ->
                        objectMapper.readValue(event.getPayload(), TransferFailedEvent.class);

                default ->
                        throw new IllegalArgumentException("Unsupported event type: " + event.getEventType());
            };
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializing outbox event payload", e);
        }
    }
}