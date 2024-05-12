package com.server.nodak.domain.userstatus.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserStatus {
    private Long userId;

    private boolean isActivate;

    private String nickname;

    private LocalDateTime lastConnected;

    public static UserStatus createUserStatus(Long userId, String nickname, boolean isActivate) {
        UserStatus userStatus = new UserStatus();
        userStatus.userId = userId;
        userStatus.isActivate = isActivate;
        userStatus.nickname = nickname;

        if (isActivate) {
            userStatus.lastConnected = LocalDateTime.now();
        }

        return userStatus;
    }

    public void updateActivate(boolean isActivate) {
        this.isActivate = isActivate;

        if (isActivate) {
            lastConnected = LocalDateTime.now();
        }
    }

}
