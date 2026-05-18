package com.banca.customer.application.port.out;

import com.banca.customer.domain.model.OutboxEvent;

import java.util.List;

public interface OutboxEventRepositoryPort {

    OutboxEvent save(OutboxEvent event);

    List<OutboxEvent> findPendingEvents(int limit);
}