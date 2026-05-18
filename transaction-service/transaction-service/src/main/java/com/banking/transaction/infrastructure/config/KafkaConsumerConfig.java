package com.banking.transaction.infrastructure.config;

import com.banking.transaction.infrastructure.messaging.event.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("!test")
public class KafkaConsumerConfig {

    private Map<String, Object> baseProps(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.banking.transaction.infrastructure.messaging.event");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return props;
    }

    private <T> ConsumerFactory<String, T> consumerFactory(
            KafkaProperties kafkaProperties,
            Class<T> eventType
    ) {
        Map<String, Object> props = baseProps(kafkaProperties);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, eventType.getName());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> listenerFactory(
            ConsumerFactory<String, T> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, AccountCreatedEvent> accountCreatedConsumerFactory(
            KafkaProperties kafkaProperties
    ) {
        return consumerFactory(kafkaProperties, AccountCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountCreatedEvent>
    accountCreatedKafkaListenerContainerFactory(
            ConsumerFactory<String, AccountCreatedEvent> accountCreatedConsumerFactory
    ) {
        return listenerFactory(accountCreatedConsumerFactory);
    }

    @Bean
    public ConsumerFactory<String, AccountActivatedEvent> accountActivatedConsumerFactory(
            KafkaProperties kafkaProperties
    ) {
        return consumerFactory(kafkaProperties, AccountActivatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountActivatedEvent>
    accountActivatedKafkaListenerContainerFactory(
            ConsumerFactory<String, AccountActivatedEvent> accountActivatedConsumerFactory
    ) {
        return listenerFactory(accountActivatedConsumerFactory);
    }

    @Bean
    public ConsumerFactory<String, AccountBlockedEvent> accountBlockedConsumerFactory(
            KafkaProperties kafkaProperties
    ) {
        return consumerFactory(kafkaProperties, AccountBlockedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountBlockedEvent>
    accountBlockedKafkaListenerContainerFactory(
            ConsumerFactory<String, AccountBlockedEvent> accountBlockedConsumerFactory
    ) {
        return listenerFactory(accountBlockedConsumerFactory);
    }

    @Bean
    public ConsumerFactory<String, AccountDebitedEvent> accountDebitedConsumerFactory(
            KafkaProperties kafkaProperties
    ) {
        return consumerFactory(kafkaProperties, AccountDebitedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountDebitedEvent>
    accountDebitedKafkaListenerContainerFactory(
            ConsumerFactory<String, AccountDebitedEvent> accountDebitedConsumerFactory
    ) {
        return listenerFactory(accountDebitedConsumerFactory);
    }

    @Bean
    public ConsumerFactory<String, AccountCreditedEvent> accountCreditedConsumerFactory(
            KafkaProperties kafkaProperties
    ) {
        return consumerFactory(kafkaProperties, AccountCreditedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountCreditedEvent>
    accountCreditedKafkaListenerContainerFactory(
            ConsumerFactory<String, AccountCreditedEvent> accountCreditedConsumerFactory
    ) {
        return listenerFactory(accountCreditedConsumerFactory);
    }
}