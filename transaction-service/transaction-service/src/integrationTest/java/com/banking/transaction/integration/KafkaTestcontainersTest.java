package com.banking.transaction.integration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class KafkaTestcontainersTest {

    @Container
    @ServiceConnection
    static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Test
    void shouldStartKafkaContainerAndCreateTopic() throws Exception {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafka.getBootstrapServers());

        try (AdminClient adminClient = AdminClient.create(properties)) {
            NewTopic topic = new NewTopic("transfer.created.test", 1, (short) 1);

            adminClient.createTopics(List.of(topic))
                    .all()
                    .get(10, TimeUnit.SECONDS);

            var topics = adminClient.listTopics()
                    .names()
                    .get(10, TimeUnit.SECONDS);

            assertTrue(topics.contains("transfer.created.test"));
        }
    }
}