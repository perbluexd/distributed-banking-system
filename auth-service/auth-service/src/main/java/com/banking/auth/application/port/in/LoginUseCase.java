package com.banking.auth.application.port.in;

import com.banking.auth.application.command.LoginCommand;
import com.banking.auth.application.model.TokenPair;

public interface LoginUseCase {

    TokenPair login(LoginCommand command);
}
