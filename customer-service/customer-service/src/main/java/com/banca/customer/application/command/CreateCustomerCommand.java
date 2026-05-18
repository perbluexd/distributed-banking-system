package com.banca.customer.application.command;

import java.util.UUID;

public record CreateCustomerCommand(
        UUID userId,
        String email
) {}