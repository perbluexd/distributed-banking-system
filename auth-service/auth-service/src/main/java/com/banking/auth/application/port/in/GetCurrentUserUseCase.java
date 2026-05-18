package com.banking.auth.application.port.in;

import com.banking.auth.domain.model.User;

import java.util.UUID;

public interface GetCurrentUserUseCase {

    User getCurrentUser(UUID userId);
}