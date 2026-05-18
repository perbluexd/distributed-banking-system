package com.banking.auth.application.port.in;

import com.banking.auth.application.command.RegisterCommand;
import com.banking.auth.domain.model.User;

public interface RegisterUseCase {

    User register(RegisterCommand command);
}
