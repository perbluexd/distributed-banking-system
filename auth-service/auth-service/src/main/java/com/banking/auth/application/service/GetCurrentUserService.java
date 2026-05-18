package com.banking.auth.application.service;

import com.banking.auth.application.error.ErrorCode;
import com.banking.auth.application.error.UnauthorizedException;
import com.banking.auth.application.port.in.GetCurrentUserUseCase;
import com.banking.auth.application.port.out.UserRepositoryPort;
import com.banking.auth.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final UserRepositoryPort userRepository;

    public GetCurrentUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser(UUID userId) {
        if (userId == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED, "User not found"));
    }
}