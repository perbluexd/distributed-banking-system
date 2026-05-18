package com.banca.customer.infrastructure.messaging.producer;

import com.banca.customer.application.port.out.EventPublisherPort;
import com.banca.customer.domain.model.OutboxEvent;
import com.banca.customer.infrastructure.messaging.event.CustomerCreatedEvent;
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
                case "CustomerCreatedEvent" ->
                        objectMapper.readValue(event.getPayload(), CustomerCreatedEvent.class);
                default ->
                        throw new IllegalArgumentException("Unsupported event type: " + event.getEventType());
            };
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializing outbox event payload", e);
        }
    }
}