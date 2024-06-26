package com.server.nodak.domain.notification.controller;

import com.server.nodak.domain.notification.service.NotificationService;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.exception.common.AuthorizationException;
import com.server.nodak.security.aop.AuthorizationRequired;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/subscribe/{userId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public SseEmitter subscribe(@PathVariable("userId") Long userId, Principal principal) {
        validateUser(userId, principal);
        return notificationService.getSseEmitter(userId);
    }

    private void validateUser(Long userId, Principal principal) {
        long currentUserId = Long.parseLong(principal.getName());
        if (currentUserId != userId) {
            throw new AuthorizationException();
        }
    }
}
