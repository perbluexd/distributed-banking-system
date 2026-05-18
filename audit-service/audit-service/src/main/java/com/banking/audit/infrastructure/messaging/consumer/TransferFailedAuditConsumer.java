package com.banking.audit.infrastructure.messaging.consumer;

import com.banking.audit.application.command.RegisterAuditRecordCommand;
import com.banking.audit.application.service.AuditEventHandlerService;
import com.banking.audit.domain.model.*;
import com.banking.audit.infrastructure.messaging.event.TransferFailedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferFailedAuditConsumer {

    private final AuditEventHandlerService auditEventHandlerService;
    private final ObjectMapper objectMapper;

    public TransferFailedAuditConsumer(AuditEventHandlerService auditEventHandlerService, ObjectMapper objectMapper) {
        this.auditEventHandlerService = auditEventHandlerService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${audit.topics.transfer-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(TransferFailedEvent event) {
        auditEventHandlerService.handle(new RegisterAuditRecordCommand(
                AuditEventId.of(event.eventId()),
                AuditEventType.TRANSFER_FAILED,
                AuditAggregateType.TRANSFER,
                AuditAggregateId.of(event.transferId()),
                AuditSourceService.TRANSACTION_SERVICE,
                AuditSeverity.ERROR,
                AuditActor.system(),
                AuditAction.TRANSFER_FAILED,
                AuditOutcome.FAILED,
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