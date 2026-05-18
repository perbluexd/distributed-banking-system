package com.banking.account.infrastructure.config;

import com.banking.account.api.mapper.AccountApiMapper;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.account.application.port.out.OutboxEventRepositoryPort;
import com.banking.account.application.service.*;
import com.banking.account.infrastructure.persistence.AccountPersistenceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public AccountPersistenceMapper accountPersistenceMapper() {
        return new AccountPersistenceMapper();
    }

    @Bean
    public AccountApiMapper accountApiMapper() {
        return new AccountApiMapper();
    }

    @Bean
    public CreateAccountService createAccountService(
            AccountRepositoryPort accountRepositoryPort,
            CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        return new CreateAccountService(
                accountRepositoryPort,
                customerSnapshotRepositoryPort,
                outboxEventRepositoryPort,
                objectMapper
        );
    }

    @Bean
    public GetAccountByIdService getAccountByIdService(
            AccountRepositoryPort accountRepositoryPort
    ) {
        return new GetAccountByIdService(accountRepositoryPort);
    }

    @Bean
    public GetAccountsByCustomerIdService getAccountsByCustomerIdService(
            AccountRepositoryPort accountRepositoryPort
    ) {
        return new GetAccountsByCustomerIdService(accountRepositoryPort);
    }

    @Bean
    public BlockAccountService blockAccountService(
            AccountRepositoryPort accountRepositoryPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        return new BlockAccountService(
                accountRepositoryPort,
                outboxEventRepositoryPort,
                objectMapper
        );
    }

    @Bean
    public ActivateAccountService activateAccountService(
            AccountRepositoryPort accountRepositoryPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        return new ActivateAccountService(
                accountRepositoryPort,
                outboxEventRepositoryPort,
                objectMapper
        );
    }
}