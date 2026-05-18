package com.banca.customer.infrastructure.persistence.mapper;

import com.banca.customer.domain.model.Customer;
import com.banca.customer.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceMapper {

    public CustomerEntity toEntity(Customer customer) {
        return new CustomerEntity(
                customer.getId(),
                customer.getUserId(),
                customer.getDni(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getStatus(),
                customer.getCreatedAt()
        );
    }

    public Customer toDomain(CustomerEntity entity) {
        return new Customer(
                entity.getId(),
                entity.getUserId(),
                entity.getDni(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}