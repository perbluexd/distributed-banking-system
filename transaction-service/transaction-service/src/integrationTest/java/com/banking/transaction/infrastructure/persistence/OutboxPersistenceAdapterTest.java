package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.domain.model.OutboxEvent;
import com.banking.transaction.domain.model.OutboxEventStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OutboxPersistenceAdapter.class)
class OutboxPersistenceAdapterTest {

    @Autowired
    private OutboxPersistenceAdapter adapter;

    @Test
    void shouldSaveAndFindPendingOutboxEvents() {
        OutboxEvent event = OutboxEvent.create(
                "Transfer",
                UUID.randomUUID(),
                "TransferCreatedEvent",
                "transfer.created",
                UUID.randomUUID().toString(),
                """
                {
                  "message": "test"
                }
                """
        );

        adapter.save(event);

        List<OutboxEvent> pendingEvents = adapter.findPendingEvents(10);

        assertEquals(1, pendingEvents.size());
        assertEquals(OutboxEventStatus.PENDING, pendingEvents.get(0).getStatus());
    }
}