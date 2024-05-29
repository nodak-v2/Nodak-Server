package com.server.nodak.domain.userstatus.service;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.domain.userstatus.domain.UserStatus;
import com.server.nodak.domain.userstatus.dto.UserStatusResponse;
import com.server.nodak.domain.userstatus.repository.UserStatusRepository;
import com.server.nodak.exception.common.DataNotFoundException;
import com.server.nodak.security.SecurityUtils;
import com.server.nodak.security.aop.AuthorizationRequired;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.server.nodak.domain.userstatus.domain.UserStatus.*;

@Service
@RequiredArgsConstructor
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @AuthorizationRequired(UserRole.GENERAL)
    public void updateHeartBeat(Long userId, boolean isActivate) {
        String nickname = getNickname(userId);
        
        if (nickname == null) {
            throw new DataNotFoundException();
        }

        UserStatus userStatus = createUserStatus(userId, nickname, isActivate);
        userStatusRepository.put(userStatus);
    }

    public List<UserStatusResponse> getAllUserStatusResponse() {
        return userStatusRepository.getAll().stream()
                .sorted(Comparator.comparing(UserStatus::getLastConnected).reversed())
                .map(UserStatusResponse::of)
                .toList();
    }

    private String getNickname(Long userId) {
        return userRepository.findById(userId)
                .map(User::getNickname)
                .orElse(null);
    }
}
