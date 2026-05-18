package com.banking.auth.application.command;

public class RefreshCommand {

    private final String refreshToken;

    public RefreshCommand(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
