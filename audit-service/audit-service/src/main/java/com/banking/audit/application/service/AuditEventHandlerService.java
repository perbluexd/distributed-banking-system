package com.banking.audit.application.service;

import com.banking.audit.application.command.RegisterAuditRecordCommand;
import com.banking.audit.application.port.in.RegisterAuditRecordUseCase;
import com.banking.audit.domain.model.AuditRecord;
import org.springframework.stereotype.Service;

@Service
public class AuditEventHandlerService {

    private final RegisterAuditRecordUseCase registerAuditRecordUseCase;

    public AuditEventHandlerService(RegisterAuditRecordUseCase registerAuditRecordUseCase) {
        this.registerAuditRecordUseCase = registerAuditRecordUseCase;
    }

    public AuditRecord handle(RegisterAuditRecordCommand command) {
        return registerAuditRecordUseCase.register(command);
    }
}