package com.banking.transaction.application.service;

import com.banking.transaction.application.port.out.EventPublisherPort;
import com.banking.transaction.application.port.out.OutboxEventRepositoryPort;
import com.banking.transaction.domain.model.OutboxEvent;
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

        List<OutboxEvent> pendingEvents =
                outboxEventRepositoryPort.findPendingEvents(BATCH_SIZE);

        log.info(
                "[TRANSACTION] Processing pending outbox events. batchSize={}, eventsFound={}",
                BATCH_SIZE,
                pendingEvents.size()
        );

        for (OutboxEvent event : pendingEvents) {
            try {

                log.info(
                        "[TRANSACTION] Publishing outbox event. eventId={}, eventType={}, topic={}, aggregateType={}, aggregateId={}, eventKey={}",
                        event.getId(),
                        event.getEventType(),
                        event.getTopic(),
                        event.getAggregateType(),
                        event.getAggregateId(),
                        event.getEventKey()
                );

                eventPublisherPort.publish(event);

                event.markAsPublished();

                outboxEventRepositoryPort.save(event);

                log.info(
                        "[TRANSACTION] Outbox event published successfully. eventId={}, eventType={}, topic={}",
                        event.getId(),
                        event.getEventType(),
                        event.getTopic()
                );

            } catch (Exception ex) {

                event.markAsFailed();

                outboxEventRepositoryPort.save(event);

                log.error(
                        "[TRANSACTION] Error publishing outbox event. eventId={}, eventType={}, topic={}",
                        event.getId(),
                        event.getEventType(),
                        event.getTopic(),
                        ex
                );
            }
        }
    }
}