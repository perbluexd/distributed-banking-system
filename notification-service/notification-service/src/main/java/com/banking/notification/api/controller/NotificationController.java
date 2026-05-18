package com.banking.notification.api.controller;

import com.banking.notification.api.dto.NotificationResponse;
import com.banking.notification.api.mapper.NotificationApiMapper;
import com.banking.notification.application.port.in.GetNotificationByIdUseCase;
import com.banking.notification.domain.model.NotificationId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final GetNotificationByIdUseCase getNotificationByIdUseCase;

    public NotificationController(
            GetNotificationByIdUseCase getNotificationByIdUseCase
    ) {
        this.getNotificationByIdUseCase = getNotificationByIdUseCase;
    }

    @GetMapping("/{notificationId}")
    public NotificationResponse getById(
            @PathVariable UUID notificationId
    ) {
        return NotificationApiMapper.toResponse(
                getNotificationByIdUseCase.getById(
                        NotificationId.of(notificationId)
                )
        );
    }
}