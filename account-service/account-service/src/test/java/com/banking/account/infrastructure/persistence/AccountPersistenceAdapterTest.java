package com.banking.account.infrastructure.persistence;

import com.banking.account.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@Import({
        AccountPersistenceAdapter.class,
        AccountPersistenceMapper.class
})
class AccountPersistenceAdapterTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("account_test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @Test
    void shouldSaveAndFindAccountById() {
        Account account = Account.create(
                CustomerId.of(UUID.randomUUID()),
                "ACC-TEST-001",
                AccountType.SAVINGS,
                Currency.PEN
        );

        Account savedAccount = accountPersistenceAdapter.save(account);

        Optional<Account> foundAccount = accountPersistenceAdapter.findById(savedAccount.getId());

        assertTrue(foundAccount.isPresent());
        assertEquals(savedAccount.getId(), foundAccount.get().getId());
        assertEquals("ACC-TEST-001", foundAccount.get().getAccountNumber());
        assertEquals(AccountType.SAVINGS, foundAccount.get().getType());
        assertEquals(AccountStatus.ACTIVE, foundAccount.get().getStatus());
        assertEquals(Currency.PEN, foundAccount.get().getCurrency());
    }

    @Test
    void shouldFindAccountsByCustomerId() {
        CustomerId customerId = CustomerId.of(UUID.randomUUID());

        Account firstAccount = Account.create(
                customerId,
                "ACC-TEST-002",
                AccountType.SAVINGS,
                Currency.PEN
        );

        Account secondAccount = Account.create(
                customerId,
                "ACC-TEST-003",
                AccountType.CHECKING,
                Currency.USD
        );

        accountPersistenceAdapter.save(firstAccount);
        accountPersistenceAdapter.save(secondAccount);

        List<Account> accounts = accountPersistenceAdapter.findByCustomerId(customerId);

        assertEquals(2, accounts.size());
    }

    @Test
    void shouldReturnEmptyListWhenCustomerHasNoAccounts() {
        CustomerId customerId = CustomerId.of(UUID.randomUUID());

        List<Account> accounts = accountPersistenceAdapter.findByCustomerId(customerId);

        assertTrue(accounts.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenAccountNumberExists() {
        Account account = Account.create(
                CustomerId.of(UUID.randomUUID()),
                "ACC-TEST-004",
                AccountType.SAVINGS,
                Currency.PEN
        );

        accountPersistenceAdapter.save(account);

        boolean exists = accountPersistenceAdapter.existsByAccountNumber("ACC-TEST-004");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenAccountNumberDoesNotExist() {
        boolean exists = accountPersistenceAdapter.existsByAccountNumber("ACC-NOT-EXISTS");

        assertFalse(exists);
    }
}