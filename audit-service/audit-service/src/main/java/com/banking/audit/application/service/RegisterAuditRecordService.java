package com.banking.audit.application.service;

import com.banking.audit.application.command.RegisterAuditRecordCommand;
import com.banking.audit.application.error.DuplicateAuditEventException;
import com.banking.audit.application.port.in.RegisterAuditRecordUseCase;
import com.banking.audit.application.port.out.AuditRecordRepositoryPort;
import com.banking.audit.domain.model.AuditRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterAuditRecordService implements RegisterAuditRecordUseCase {

    private final AuditRecordRepositoryPort auditRecordRepositoryPort;

    public RegisterAuditRecordService(AuditRecordRepositoryPort auditRecordRepositoryPort) {
        this.auditRecordRepositoryPort = auditRecordRepositoryPort;
    }

    @Override
    @Transactional
    public AuditRecord register(RegisterAuditRecordCommand command) {
        if (auditRecordRepositoryPort.existsByEventId(command.eventId())) {
            throw new DuplicateAuditEventException(
                    "Audit event already registered: " + command.eventId().value()
            );
        }

        AuditRecord auditRecord = AuditRecord.register(
                command.eventId(),
                command.eventType(),
                command.aggregateType(),
                command.aggregateId(),
                command.sourceService(),
                command.severity(),
                command.actor(),
                command.action(),
                command.outcome(),
                command.payloadJson(),
                command.occurredAt()
        );

        return auditRecordRepositoryPort.save(auditRecord);
    }
}