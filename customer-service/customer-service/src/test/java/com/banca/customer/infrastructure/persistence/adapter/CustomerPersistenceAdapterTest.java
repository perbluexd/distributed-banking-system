package com.banca.customer.infrastructure.persistence.adapter;

import com.banca.customer.domain.model.Customer;
import com.banca.customer.infrastructure.persistence.entity.CustomerEntity;
import com.banca.customer.infrastructure.persistence.mapper.CustomerPersistenceMapper;
import com.banca.customer.infrastructure.persistence.repository.CustomerJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerPersistenceAdapterTest {

    @Mock
    private CustomerJpaRepository customerJpaRepository;

    private CustomerPersistenceMapper mapper;
    private CustomerPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = new CustomerPersistenceMapper();
        adapter = new CustomerPersistenceAdapter(customerJpaRepository, mapper);
    }

    @Test
    void shouldSaveCustomer() {
        Customer customer = Customer.create(UUID.randomUUID(), "test@mail.com");

        when(customerJpaRepository.save(any(CustomerEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Customer result = adapter.save(customer);

        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getUserId(), result.getUserId());
        assertEquals(customer.getEmail(), result.getEmail());

        verify(customerJpaRepository).save(any(CustomerEntity.class));
    }

    @Test
    void shouldFindByUserId() {
        Customer customer = Customer.create(UUID.randomUUID(), "test@mail.com");
        CustomerEntity entity = mapper.toEntity(customer);

        when(customerJpaRepository.findByUserId(customer.getUserId()))
                .thenReturn(Optional.of(entity));

        Optional<Customer> result = adapter.findByUserId(customer.getUserId());

        assertTrue(result.isPresent());
        assertEquals(customer.getUserId(), result.get().getUserId());
    }
}