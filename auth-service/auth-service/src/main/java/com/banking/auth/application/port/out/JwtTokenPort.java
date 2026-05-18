package com.banking.auth.application.port.out;

import com.banking.auth.domain.model.User;

public interface JwtTokenPort {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);
}
