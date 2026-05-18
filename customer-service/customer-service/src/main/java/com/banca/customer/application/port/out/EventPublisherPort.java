package com.banca.customer.application.port.out;

import com.banca.customer.domain.model.OutboxEvent;

public interface EventPublisherPort {

    void publish(OutboxEvent event);
}