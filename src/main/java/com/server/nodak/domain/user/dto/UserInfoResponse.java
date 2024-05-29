package com.server.nodak.domain.user.dto;

import com.server.nodak.domain.user.domain.User;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoResponse {
    private boolean isLogin;
    private Long userId;
    private String nickname;
    private String profileImage;

    public static UserInfoResponse of(User user) {
        return new UserInfoResponse(
                user != null,
                user != null ? user.getId() : null,
                user != null ? user.getNickname() : null,
                user != null ? user.getProfileImageUrl() : null
        );
    }
}
