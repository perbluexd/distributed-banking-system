package com.banking.audit.infrastructure.messaging.consumer;

import com.banking.audit.application.command.RegisterAuditRecordCommand;
import com.banking.audit.application.service.AuditEventHandlerService;
import com.banking.audit.domain.model.*;
import com.banking.audit.infrastructure.messaging.event.AccountCreditedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AccountCreditedAuditConsumer {

    private final AuditEventHandlerService auditEventHandlerService;
    private final ObjectMapper objectMapper;

    public AccountCreditedAuditConsumer(AuditEventHandlerService auditEventHandlerService, ObjectMapper objectMapper) {
        this.auditEventHandlerService = auditEventHandlerService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${audit.topics.account-credited}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(AccountCreditedEvent event) {
        auditEventHandlerService.handle(new RegisterAuditRecordCommand(
                AuditEventId.of(event.eventId()),
                AuditEventType.ACCOUNT_CREDITED,
                AuditAggregateType.ACCOUNT,
                AuditAggregateId.of(event.accountId()),
                AuditSourceService.ACCOUNT_SERVICE,
                AuditSeverity.INFO,
                AuditActor.system(),
                AuditAction.CREDITED,
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