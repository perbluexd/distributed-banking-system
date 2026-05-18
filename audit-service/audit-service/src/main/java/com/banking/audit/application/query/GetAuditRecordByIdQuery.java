package com.banking.audit.application.query;

import com.banking.audit.domain.model.AuditRecordId;

import java.util.Objects;

public record GetAuditRecordByIdQuery(
        AuditRecordId auditRecordId
) {

    public GetAuditRecordByIdQuery {
        Objects.requireNonNull(auditRecordId, "Audit record id cannot be null");
    }
}