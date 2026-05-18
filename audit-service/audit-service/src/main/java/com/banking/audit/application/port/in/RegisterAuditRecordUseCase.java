package com.banking.audit.application.port.in;

import com.banking.audit.application.command.RegisterAuditRecordCommand;
import com.banking.audit.domain.model.AuditRecord;

public interface RegisterAuditRecordUseCase {

    AuditRecord register(RegisterAuditRecordCommand command);
}