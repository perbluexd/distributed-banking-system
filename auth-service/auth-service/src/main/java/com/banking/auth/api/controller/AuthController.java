package com.banking.auth.api.controller;

import com.banking.auth.api.dto.AuthResponse;
import com.banking.auth.api.dto.LoginRequest;
import com.banking.auth.api.dto.RefreshRequest;
import com.banking.auth.api.dto.RegisterRequest;
import com.banking.auth.api.dto.UserResponse;
import com.banking.auth.api.mapper.AuthApiMapper;
import com.banking.auth.application.model.TokenPair;
import com.banking.auth.application.port.in.GetCurrentUserUseCase;
import com.banking.auth.application.port.in.LoginUseCase;
import com.banking.auth.application.port.in.RefreshUseCase;
import com.banking.auth.application.port.in.RegisterUseCase;
import com.banking.auth.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for register, login, refresh and current user")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshUseCase refreshUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final AuthApiMapper mapper;

    public AuthController(RegisterUseCase registerUseCase,
                          LoginUseCase loginUseCase,
                          RefreshUseCase refreshUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase,
                          AuthApiMapper mapper) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshUseCase = refreshUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")// Post indica crear una operación con datos
    @ResponseStatus(HttpStatus.CREATED)// Esto le dice a spring que si este método termina bien, la respuesta tendrá codigo 201 created
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUseCase.register(mapper.toCommand(request));
        return mapper.toResponse(user);
    }

    @Operation(summary = "Login and obtain access/refresh tokens")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        TokenPair tokenPair = loginUseCase.login(mapper.toCommand(request));
        return mapper.toResponse(tokenPair);
    }

    @Operation(summary = "Refresh access token using refresh token")
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        TokenPair tokenPair = refreshUseCase.refresh(mapper.toCommand(request));
        return mapper.toResponse(tokenPair);
    }

    @Operation(summary = "Get current authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        User user = getCurrentUserUseCase.getCurrentUser(userId);
        return mapper.toResponse(user);
    }
}