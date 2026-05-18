package com.banking.auth.application.command;

public class RegisterCommand {

    private final String email;
    private final String password;

    public RegisterCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
