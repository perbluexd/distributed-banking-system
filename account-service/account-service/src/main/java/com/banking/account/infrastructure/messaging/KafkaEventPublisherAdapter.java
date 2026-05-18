package com.banking.account.infrastructure.messaging;

import com.banking.account.application.port.out.EventPublisherPort;
import com.banking.account.domain.model.OutboxEvent;
import com.banking.account.infrastructure.messaging.event.*;
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
                case "AccountCreatedEvent" -> objectMapper.readValue(event.getPayload(), AccountCreatedEvent.class);
                case "AccountActivatedEvent" -> objectMapper.readValue(event.getPayload(), AccountActivatedEvent.class);
                case "AccountBlockedEvent" -> objectMapper.readValue(event.getPayload(), AccountBlockedEvent.class);
                case "AccountDebitedEvent" -> objectMapper.readValue(event.getPayload(), AccountDebitedEvent.class);
                case "AccountCreditedEvent" -> objectMapper.readValue(event.getPayload(), AccountCreditedEvent.class);
                default -> throw new IllegalArgumentException("Unsupported event type: " + event.getEventType());
            };
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializing outbox event payload", e);
        }
    }
}