package com.banking.audit.infrastructure.messaging.consumer;

import com.banking.audit.application.command.RegisterAuditRecordCommand;
import com.banking.audit.application.service.AuditEventHandlerService;
import com.banking.audit.domain.model.*;
import com.banking.audit.infrastructure.messaging.event.UserLoggedInEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserLoggedInAuditConsumer {

    private final AuditEventHandlerService auditEventHandlerService;
    private final ObjectMapper objectMapper;

    public UserLoggedInAuditConsumer(AuditEventHandlerService auditEventHandlerService, ObjectMapper objectMapper) {
        this.auditEventHandlerService = auditEventHandlerService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${audit.topics.user-logged-in}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(UserLoggedInEvent event) {
        auditEventHandlerService.handle(new RegisterAuditRecordCommand(
                AuditEventId.of(event.eventId()),
                AuditEventType.USER_LOGGED_IN,
                AuditAggregateType.USER,
                AuditAggregateId.of(event.userId()),
                AuditSourceService.AUTH_SERVICE,
                AuditSeverity.INFO,
                AuditActor.user(event.userId()),
                AuditAction.LOGGED_IN,
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