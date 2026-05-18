package com.banking.auth.application.port.out;

import com.banking.auth.domain.model.OutboxEvent;

import java.util.List;

public interface OutboxEventRepositoryPort {

    OutboxEvent save(OutboxEvent event);

    List<OutboxEvent> findPendingEvents(int limit);
}