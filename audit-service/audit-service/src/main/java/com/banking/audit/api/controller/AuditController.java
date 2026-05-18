package com.banking.audit.api.controller;

import com.banking.audit.api.dto.AuditRecordResponse;
import com.banking.audit.api.dto.AuditRecordSearchRequest;
import com.banking.audit.api.mapper.AuditApiMapper;
import com.banking.audit.application.port.in.GetAuditRecordByIdUseCase;
import com.banking.audit.application.port.in.SearchAuditRecordsUseCase;
import com.banking.audit.application.query.GetAuditRecordByIdQuery;
import com.banking.audit.domain.model.AuditRecordId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audit-records")
public class AuditController {

    private final GetAuditRecordByIdUseCase getAuditRecordByIdUseCase;
    private final SearchAuditRecordsUseCase searchAuditRecordsUseCase;
    private final AuditApiMapper auditApiMapper;

    public AuditController(
            GetAuditRecordByIdUseCase getAuditRecordByIdUseCase,
            SearchAuditRecordsUseCase searchAuditRecordsUseCase,
            AuditApiMapper auditApiMapper
    ) {
        this.getAuditRecordByIdUseCase = getAuditRecordByIdUseCase;
        this.searchAuditRecordsUseCase = searchAuditRecordsUseCase;
        this.auditApiMapper = auditApiMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditRecordResponse> getById(@PathVariable UUID id) {
        var auditRecord = getAuditRecordByIdUseCase.getById(
                new GetAuditRecordByIdQuery(AuditRecordId.of(id))
        );

        return ResponseEntity.ok(auditApiMapper.toResponse(auditRecord));
    }

    @GetMapping
    public ResponseEntity<List<AuditRecordResponse>> search(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String aggregateType,
            @RequestParam(required = false) UUID aggregateId,
            @RequestParam(required = false) String sourceService,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String outcome,
            @RequestParam(required = false) java.time.Instant occurredFrom,
            @RequestParam(required = false) java.time.Instant occurredTo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        var request = new AuditRecordSearchRequest(
                eventType,
                aggregateType,
                aggregateId,
                sourceService,
                severity,
                outcome,
                occurredFrom,
                occurredTo,
                page,
                size
        );

        var query = auditApiMapper.toQuery(request);

        var response = searchAuditRecordsUseCase.search(query)
                .stream()
                .map(auditApiMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}