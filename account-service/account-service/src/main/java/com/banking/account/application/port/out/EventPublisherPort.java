package com.banking.account.application.port.out;

import com.banking.account.domain.model.OutboxEvent;

public interface EventPublisherPort {

    void publish(OutboxEvent event);
}