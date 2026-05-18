package com.banking.account.application.service;

import com.banking.account.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.account.domain.model.CustomerSnapshot;
import com.banking.account.infrastructure.messaging.event.CustomerCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerEventHandlerService {

    private static final Logger log =
            LoggerFactory.getLogger(CustomerEventHandlerService.class);

    private final CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort;
    private final CustomerOnboardingService customerOnboardingService;

    public CustomerEventHandlerService(
            CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort,
            CustomerOnboardingService customerOnboardingService
    ) {
        this.customerSnapshotRepositoryPort = customerSnapshotRepositoryPort;
        this.customerOnboardingService = customerOnboardingService;
    }

    @Transactional
    public void handleCustomerCreated(CustomerCreatedEvent event) {

        CustomerSnapshot snapshot = CustomerSnapshot.create(
                event.customerId(),
                event.userId(),
                event.email(),
                event.status(),
                event.occurredAt(),
                event.occurredAt()
        );

        customerSnapshotRepositoryPort.save(snapshot);

        log.info(
                "CustomerSnapshot saved. customerId={}, userId={}, status={}",
                event.customerId(),
                event.userId(),
                event.status()
        );

        customerOnboardingService.onboardCustomer(event.customerId());
    }
}