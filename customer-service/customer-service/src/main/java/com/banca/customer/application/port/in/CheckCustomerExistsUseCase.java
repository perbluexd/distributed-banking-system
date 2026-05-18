package com.banca.customer.application.port.in;

import java.util.UUID;

public interface CheckCustomerExistsUseCase {

    boolean existsById(UUID customerId);
}