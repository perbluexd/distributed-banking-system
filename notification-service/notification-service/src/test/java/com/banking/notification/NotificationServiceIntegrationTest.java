package com.banking.notification;

import com.banking.notification.application.port.out.EmailSenderPort;
import com.banking.notification.domain.model.NotificationStatus;
import com.banking.notification.domain.model.NotificationType;
import com.banking.notification.infrastructure.messaging.event.CustomerCreatedEvent;
import com.banking.notification.infrastructure.messaging.event.TransferCompletedEvent;
import com.banking.notification.infrastructure.persistence.repository.CustomerSnapshotJpaRepository;
import com.banking.notification.infrastructure.persistence.repository.NotificationJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine")
    )
            .withDatabaseName("notificationdb")
            .withUsername("notificationuser")
            .withPassword("notificationpass");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
    );

    @Container
    static GenericContainer<?> mailhog = new GenericContainer<>(
            DockerImageName.parse("mailhog/mailhog:v1.0.1")
    )
            .withExposedPorts(1025, 8025)
            .waitingFor(Wait.forHttp("/api/v2/messages").forPort(8025));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        registry.add("spring.mail.host", mailhog::getHost);
        registry.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
    }

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired
    private CustomerSnapshotJpaRepository customerSnapshotJpaRepository;

    @Autowired
    private NotificationJpaRepository notificationJpaRepository;

    @MockitoBean
    private EmailSenderPort emailSenderPort;

    @Test
    void shouldProcessCustomerCreatedAndTransferCompletedAndSendEmail() throws Exception {
        doNothing().when(emailSenderPort).send(any());

        UUID customerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();

        CustomerCreatedEvent customerCreatedEvent = new CustomerCreatedEvent(
                customerId,
                userId,
                "customer@example.com",
                "ACTIVE",
                Instant.now()
        );

        kafkaTemplate.send(
                "customer.created",
                customerId.toString(),
                customerCreatedEvent
        ).get();

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() ->
                        assertTrue(customerSnapshotJpaRepository.existsByCustomerId(customerId))
                );

        TransferCompletedEvent transferCompletedEvent = new TransferCompletedEvent(
                transferId,
                customerId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(150),
                "PEN",
                "COMPLETED",
                Instant.now()
        );

        kafkaTemplate.send(
                "transfer.completed",
                transferId.toString(),
                transferCompletedEvent
        ).get();

        await()
                .atMost(Duration.ofSeconds(20))
                .untilAsserted(() -> {
                    var optionalNotification = notificationJpaRepository
                            .findByTransferIdAndType(
                                    transferId,
                                    NotificationType.TRANSFER_COMPLETED
                            );

                    assertTrue(optionalNotification.isPresent());

                    var notification = optionalNotification.get();

                    assertEquals(NotificationStatus.SENT, notification.getStatus());
                    assertEquals("customer@example.com", notification.getRecipientEmail());
                    assertEquals(1, notification.getAttemptCount());
                    assertNull(notification.getErrorMessage());
                    assertNotNull(notification.getSentAt());
                });

        verify(emailSenderPort).send(any());
    }
}