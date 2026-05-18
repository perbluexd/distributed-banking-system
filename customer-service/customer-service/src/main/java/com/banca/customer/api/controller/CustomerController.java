package com.banca.customer.api.controller;

import com.banca.customer.api.dto.CustomerExistsResponse;
import com.banca.customer.api.mapper.CustomerApiMapper;
import com.banca.customer.application.port.in.CheckCustomerExistsUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CheckCustomerExistsUseCase checkCustomerExistsUseCase;
    private final CustomerApiMapper customerApiMapper;

    public CustomerController(
            CheckCustomerExistsUseCase checkCustomerExistsUseCase,
            CustomerApiMapper customerApiMapper
    ) {
        this.checkCustomerExistsUseCase = checkCustomerExistsUseCase;
        this.customerApiMapper = customerApiMapper;
    }

    @GetMapping("/{customerId}/exists")
    public CustomerExistsResponse existsById(@PathVariable UUID customerId) {
        boolean exists = checkCustomerExistsUseCase.existsById(customerId);
        return customerApiMapper.toExistsResponse(exists);
    }
}