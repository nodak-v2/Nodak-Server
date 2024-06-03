package com.server.nodak.domain.user.dto;

import com.server.nodak.domain.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoResponse {
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int followerCount;
    private int followeeCount;

    protected UserInfoResponse(User user, int followerCount, int followeeCount) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.introduction = user.getDescription();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.followerCount = followerCount;
        this.followeeCount = followeeCount;
    }

    public static UserInfoResponse of(User user, int followerCount, int followeeCount) {
        return new UserInfoResponse(user, followerCount, followeeCount);
    }
}
