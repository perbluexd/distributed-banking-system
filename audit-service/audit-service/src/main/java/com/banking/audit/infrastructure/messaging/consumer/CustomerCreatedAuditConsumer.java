package com.banking.audit.infrastructure.messaging.consumer;

import com.banking.audit.application.command.RegisterAuditRecordCommand;
import com.banking.audit.application.service.AuditEventHandlerService;
import com.banking.audit.domain.model.*;
import com.banking.audit.infrastructure.messaging.event.CustomerCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreatedAuditConsumer {

    private final AuditEventHandlerService auditEventHandlerService;
    private final ObjectMapper objectMapper;

    public CustomerCreatedAuditConsumer(AuditEventHandlerService auditEventHandlerService, ObjectMapper objectMapper) {
        this.auditEventHandlerService = auditEventHandlerService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${audit.topics.customer-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(CustomerCreatedEvent event) {
        auditEventHandlerService.handle(new RegisterAuditRecordCommand(
                AuditEventId.of(event.eventId()),
                AuditEventType.CUSTOMER_CREATED,
                AuditAggregateType.CUSTOMER,
                AuditAggregateId.of(event.customerId()),
                AuditSourceService.CUSTOMER_SERVICE,
                AuditSeverity.INFO,
                AuditActor.user(event.userId()),
                AuditAction.CREATED,
                AuditOutcome.SUCCESS,
                toJson(event),
                event.occurredAt()
        ));
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize audit event", e);
        }
    }
}