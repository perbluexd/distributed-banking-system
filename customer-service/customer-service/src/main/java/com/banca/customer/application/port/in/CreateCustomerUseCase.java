package com.banca.customer.application.port.in;

import com.banca.customer.application.command.CreateCustomerCommand;
import com.banca.customer.domain.model.Customer;

public interface CreateCustomerUseCase {

    Customer create(CreateCustomerCommand command);

}