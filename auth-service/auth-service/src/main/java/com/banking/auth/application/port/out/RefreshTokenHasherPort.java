package com.banking.auth.application.port.out;

public interface RefreshTokenHasherPort {
    String hash(String refreshToken);
}
