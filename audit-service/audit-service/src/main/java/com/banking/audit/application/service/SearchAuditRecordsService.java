package com.banking.audit.application.service;

import com.banking.audit.application.port.in.SearchAuditRecordsUseCase;
import com.banking.audit.application.port.out.AuditRecordRepositoryPort;
import com.banking.audit.application.query.SearchAuditRecordsQuery;
import com.banking.audit.domain.model.AuditRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchAuditRecordsService implements SearchAuditRecordsUseCase {

    private final AuditRecordRepositoryPort auditRecordRepositoryPort;

    public SearchAuditRecordsService(AuditRecordRepositoryPort auditRecordRepositoryPort) {
        this.auditRecordRepositoryPort = auditRecordRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditRecord> search(SearchAuditRecordsQuery query) {
        return auditRecordRepositoryPort.search(query);
    }
}