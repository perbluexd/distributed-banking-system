package com.banca.customer.api.mapper;

import com.banca.customer.api.dto.CustomerExistsResponse;
import com.banca.customer.api.dto.CustomerResponse;
import com.banca.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerApiMapper {

    public CustomerExistsResponse toExistsResponse(boolean exists) {
        return new CustomerExistsResponse(exists);
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getUserId(),
                customer.getDni(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getStatus(),
                customer.getCreatedAt()
        );
    }
}