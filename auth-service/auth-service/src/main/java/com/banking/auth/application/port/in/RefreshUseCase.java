package com.banking.auth.application.port.in;

import com.banking.auth.application.command.RefreshCommand;
import com.banking.auth.application.model.TokenPair;

public interface RefreshUseCase {

    TokenPair refresh(RefreshCommand command);
}
