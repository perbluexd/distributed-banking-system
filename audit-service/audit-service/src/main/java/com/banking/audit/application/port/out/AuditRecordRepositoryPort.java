package com.banking.audit.application.port.out;

import com.banking.audit.application.query.SearchAuditRecordsQuery;
import com.banking.audit.domain.model.AuditEventId;
import com.banking.audit.domain.model.AuditRecord;
import com.banking.audit.domain.model.AuditRecordId;

import java.util.List;
import java.util.Optional;

public interface AuditRecordRepositoryPort {

    AuditRecord save(AuditRecord auditRecord);

    Optional<AuditRecord> findById(AuditRecordId auditRecordId);

    boolean existsByEventId(AuditEventId eventId);

    List<AuditRecord> search(SearchAuditRecordsQuery query);
}