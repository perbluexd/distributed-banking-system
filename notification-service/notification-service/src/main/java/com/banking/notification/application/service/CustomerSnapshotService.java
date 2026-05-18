package com.banking.notification.application.service;

import com.banking.notification.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.notification.domain.model.CustomerId;
import com.banking.notification.domain.model.CustomerSnapshot;
import com.banking.notification.infrastructure.messaging.event.CustomerCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerSnapshotService {

    private static final Logger log = LoggerFactory.getLogger(CustomerSnapshotService.class);

    private final CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort;

    public CustomerSnapshotService(
            CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort
    ) {
        this.customerSnapshotRepositoryPort = customerSnapshotRepositoryPort;
    }

    @Transactional
    public void handleCustomerCreated(CustomerCreatedEvent event) {
        CustomerId customerId = CustomerId.of(event.customerId());

        customerSnapshotRepositoryPort
                .findByCustomerId(customerId)
                .ifPresentOrElse(
                        existingSnapshot -> updateExistingSnapshot(existingSnapshot, event),
                        () -> createNewSnapshot(event)
                );
    }

    private void createNewSnapshot(CustomerCreatedEvent event) {
        CustomerSnapshot snapshot = CustomerSnapshot.create(
                CustomerId.of(event.customerId()),
                event.userId(),
                event.email(),
                event.status()
        );

        customerSnapshotRepositoryPort.save(snapshot);

        log.info(
                "[NOTIFICATION] CustomerSnapshot created. customerId={}, userId={}, email={}",
                event.customerId(),
                event.userId(),
                event.email()
        );
    }

    private void updateExistingSnapshot(
            CustomerSnapshot existingSnapshot,
            CustomerCreatedEvent event
    ) {
        existingSnapshot.update(
                event.email(),
                event.status()
        );

        customerSnapshotRepositoryPort.save(existingSnapshot);

        log.info(
                "[NOTIFICATION] CustomerSnapshot updated. customerId={}, userId={}, email={}",
                event.customerId(),
                event.userId(),
                event.email()
        );
    }
}