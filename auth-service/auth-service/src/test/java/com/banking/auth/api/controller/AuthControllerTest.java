package com.banking.auth.api.controller;

import com.banking.auth.api.dto.AuthResponse;
import com.banking.auth.api.dto.LoginRequest;
import com.banking.auth.api.dto.RefreshRequest;
import com.banking.auth.api.dto.RegisterRequest;
import com.banking.auth.api.dto.UserResponse;
import com.banking.auth.api.error.GlobalExceptionHandler;
import com.banking.auth.api.mapper.AuthApiMapper;
import com.banking.auth.application.command.LoginCommand;
import com.banking.auth.application.command.RefreshCommand;
import com.banking.auth.application.command.RegisterCommand;
import com.banking.auth.application.error.ConflictException;
import com.banking.auth.application.error.ErrorCode;
import com.banking.auth.application.model.TokenPair;
import com.banking.auth.application.port.in.GetCurrentUserUseCase;
import com.banking.auth.application.port.in.LoginUseCase;
import com.banking.auth.application.port.in.RefreshUseCase;
import com.banking.auth.application.port.in.RegisterUseCase;
import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUseCase registerUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private RefreshUseCase refreshUseCase;

    @MockitoBean
    private GetCurrentUserUseCase getCurrentUserUseCase;

    @MockitoBean
    private AuthApiMapper mapper;

    @Test
    @DisplayName("POST /auth/register -> 201 Created")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterCommand command = mock(RegisterCommand.class);
        User user = new User(
                UUID.randomUUID(),
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );
        UserResponse response = mock(UserResponse.class);

        when(mapper.toCommand(any(RegisterRequest.class))).thenReturn(command);
        when(registerUseCase.register(command)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@mail.com",
                                  "password": "12345678"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(mapper).toCommand(any(RegisterRequest.class));
        verify(registerUseCase).register(command);
        verify(mapper).toResponse(user);
    }

    @Test
    @DisplayName("POST /auth/register -> 400 Bad Request when DTO is invalid")
    void shouldReturnBadRequestWhenRegisterRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(registerUseCase);
    }

    @Test
    @DisplayName("POST /auth/register -> 409 Conflict when email already exists")
    void shouldReturnConflictWhenRegisterThrowsConflictException() throws Exception {
        RegisterCommand command = mock(RegisterCommand.class);

        when(mapper.toCommand(any(RegisterRequest.class))).thenReturn(command);
        when(registerUseCase.register(command))
                .thenThrow(new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@mail.com",
                                  "password": "12345678"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    @DisplayName("POST /auth/login -> 200 OK")
    void shouldLoginSuccessfully() throws Exception {
        LoginCommand command = mock(LoginCommand.class);
        TokenPair tokenPair = new TokenPair("access-token", "refresh-token", "Bearer", 900);
        AuthResponse response = mock(AuthResponse.class);

        when(mapper.toCommand(any(LoginRequest.class))).thenReturn(command);
        when(loginUseCase.login(command)).thenReturn(tokenPair);
        when(mapper.toResponse(tokenPair)).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@mail.com",
                                  "password": "12345678"
                                }
                                """))
                .andExpect(status().isOk());

        verify(mapper).toCommand(any(LoginRequest.class));
        verify(loginUseCase).login(command);
        verify(mapper).toResponse(tokenPair);
    }

    @Test
    @DisplayName("POST /auth/login -> 400 Bad Request when DTO is invalid")
    void shouldReturnBadRequestWhenLoginRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(loginUseCase);
    }

    @Test
    @DisplayName("POST /auth/refresh -> 200 OK")
    void shouldRefreshSuccessfully() throws Exception {
        RefreshCommand command = mock(RefreshCommand.class);
        TokenPair tokenPair = new TokenPair("new-access-token", "refresh-token", "Bearer", 900);
        AuthResponse response = mock(AuthResponse.class);

        when(mapper.toCommand(any(RefreshRequest.class))).thenReturn(command);
        when(refreshUseCase.refresh(command)).thenReturn(tokenPair);
        when(mapper.toResponse(tokenPair)).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isOk());

        verify(mapper).toCommand(any(RefreshRequest.class));
        verify(refreshUseCase).refresh(command);
        verify(mapper).toResponse(tokenPair);
    }

    @Test
    @DisplayName("POST /auth/refresh -> 400 Bad Request when refresh token is blank")
    void shouldReturnBadRequestWhenRefreshRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verifyNoInteractions(refreshUseCase);
    }

    @Test
    @DisplayName("GET /auth/me -> 200 OK")
    void shouldReturnCurrentAuthenticatedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );
        UserResponse response = mock(UserResponse.class);

        when(getCurrentUserUseCase.getCurrentUser(userId)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(get("/auth/me")
                        .principal(new UsernamePasswordAuthenticationToken(userId.toString(), null)))
                .andExpect(status().isOk());

        verify(getCurrentUserUseCase).getCurrentUser(userId);
        verify(mapper).toResponse(user);
    }
}