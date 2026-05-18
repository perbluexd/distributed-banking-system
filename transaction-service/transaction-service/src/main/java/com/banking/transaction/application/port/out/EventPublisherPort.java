package com.banking.transaction.application.port.out;

import com.banking.transaction.domain.model.OutboxEvent;

public interface EventPublisherPort {

    void publish(OutboxEvent event);
}