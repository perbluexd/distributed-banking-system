package com.banking.auth.application.port.out;

import com.banking.auth.domain.model.OutboxEvent;

public interface EventPublisherPort {

    void publish(OutboxEvent event);
}