package com.banca.customer.application.service;

import com.banca.customer.application.port.out.EventPublisherPort;
import com.banca.customer.application.port.out.OutboxEventRepositoryPort;
import com.banca.customer.domain.model.OutboxEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OutboxProcessorService {

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessorService.class);
    private static final int BATCH_SIZE = 20;

    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final EventPublisherPort eventPublisherPort;

    public OutboxProcessorService(
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            EventPublisherPort eventPublisherPort
    ) {
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Scheduled(fixedDelayString = "${outbox.processor.fixed-delay-ms:5000}")
    @Transactional
    public void processPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepositoryPort.findPendingEvents(BATCH_SIZE);

        for (OutboxEvent event : pendingEvents) {
            try {
                eventPublisherPort.publish(event);
                event.markAsPublished();
                outboxEventRepositoryPort.save(event);

                log.info(
                        "Outbox event published. id={}, type={}, topic={}",
                        event.getId(),
                        event.getEventType(),
                        event.getTopic()
                );

            } catch (Exception ex) {
                event.markAsFailed();
                outboxEventRepositoryPort.save(event);

                log.error(
                        "Error publishing outbox event. id={}, type={}",
                        event.getId(),
                        event.getEventType(),
                        ex
                );
            }
        }
    }
}