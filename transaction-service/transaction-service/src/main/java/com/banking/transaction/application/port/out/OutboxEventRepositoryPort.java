package com.banking.transaction.application.port.out;

import com.banking.transaction.domain.model.OutboxEvent;

import java.util.List;

public interface OutboxEventRepositoryPort {

    OutboxEvent save(OutboxEvent event);

    List<OutboxEvent> findPendingEvents(int limit);
}