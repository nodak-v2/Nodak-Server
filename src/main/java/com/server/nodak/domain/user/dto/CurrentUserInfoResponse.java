package com.server.nodak.domain.user.dto;

import com.server.nodak.domain.user.domain.User;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrentUserInfoResponse {
    private boolean isLogin;
    private Long userId;
    private String nickname;
    private String profileImage;

    public static CurrentUserInfoResponse of(User user) {
        return new CurrentUserInfoResponse(
                user != null,
                user != null ? user.getId() : null,
                user != null ? user.getNickname() : null,
                user != null ? user.getProfileImageUrl() : null
        );
    }
}
