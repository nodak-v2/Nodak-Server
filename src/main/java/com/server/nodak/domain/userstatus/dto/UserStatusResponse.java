package com.server.nodak.domain.userstatus.dto;

import com.server.nodak.domain.userstatus.domain.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserStatusResponse {
    private Long userId;
    private String nickname;
    private boolean isActivate;

    public static UserStatusResponse of(UserStatus userStatus) {
        return new UserStatusResponse(userStatus.getUserId(), userStatus.getNickname(), userStatus.isActivate());
    }
}
