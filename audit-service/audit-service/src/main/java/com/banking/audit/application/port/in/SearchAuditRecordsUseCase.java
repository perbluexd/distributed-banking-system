package com.banking.audit.application.port.in;

import com.banking.audit.application.query.SearchAuditRecordsQuery;
import com.banking.audit.domain.model.AuditRecord;

import java.util.List;

public interface SearchAuditRecordsUseCase {

    List<AuditRecord> search(SearchAuditRecordsQuery query);
}