package com.banca.customer.application.service;

import com.banca.customer.application.port.in.CheckCustomerExistsUseCase;
import com.banca.customer.application.port.out.CustomerRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CheckCustomerExistsService implements CheckCustomerExistsUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    public CheckCustomerExistsService(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    public boolean existsById(UUID customerId) {
        return customerRepositoryPort.existsById(customerId);
    }
}