package com.banking.audit.application.port.in;

import com.banking.audit.application.query.GetAuditRecordByIdQuery;
import com.banking.audit.domain.model.AuditRecord;

public interface GetAuditRecordByIdUseCase {

    AuditRecord getById(GetAuditRecordByIdQuery query);
}