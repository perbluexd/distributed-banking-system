package com.banking.auth.api.mapper;

import com.banking.auth.api.dto.AuthResponse;
import com.banking.auth.api.dto.LoginRequest;
import com.banking.auth.api.dto.RefreshRequest;
import com.banking.auth.api.dto.RegisterRequest;
import com.banking.auth.api.dto.UserResponse;
import com.banking.auth.application.command.LoginCommand;
import com.banking.auth.application.command.RefreshCommand;
import com.banking.auth.application.command.RegisterCommand;
import com.banking.auth.application.model.TokenPair;
import com.banking.auth.domain.model.User;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthApiMapper {

    // Register
    RegisterCommand toCommand(RegisterRequest request);

    // Login
    LoginCommand toCommand(LoginRequest request);

    // Refresh
    RefreshCommand toCommand(RefreshRequest request);

    // TokenPair -> AuthResponse
    default AuthResponse toResponse(TokenPair tokenPair) {
        return new AuthResponse(
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken(),
                tokenPair.getTokenType(),
                tokenPair.getExpiresIn()
        );
    }

    // User -> UserResponse
    default UserResponse toResponse(User user) {
        Set<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                roles,
                user.getCreatedAt()
        );
    }
}
