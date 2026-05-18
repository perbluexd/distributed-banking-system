package com.banking.audit.application.service;

import com.banking.audit.application.error.AuditRecordNotFoundException;
import com.banking.audit.application.port.in.GetAuditRecordByIdUseCase;
import com.banking.audit.application.port.out.AuditRecordRepositoryPort;
import com.banking.audit.application.query.GetAuditRecordByIdQuery;
import com.banking.audit.domain.model.AuditRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAuditRecordByIdService implements GetAuditRecordByIdUseCase {

    private final AuditRecordRepositoryPort auditRecordRepositoryPort;

    public GetAuditRecordByIdService(AuditRecordRepositoryPort auditRecordRepositoryPort) {
        this.auditRecordRepositoryPort = auditRecordRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public AuditRecord getById(GetAuditRecordByIdQuery query) {
        return auditRecordRepositoryPort.findById(query.auditRecordId())
                .orElseThrow(() -> new AuditRecordNotFoundException(
                        "Audit record not found: " + query.auditRecordId().value()
                ));
    }
}