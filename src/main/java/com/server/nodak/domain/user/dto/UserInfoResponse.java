package com.server.nodak.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.server.nodak.domain.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;
    private int followerCount;
    private int followeeCount;

    protected UserInfoResponse(User user, int followerCount, int followeeCount) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.introduction = user.getDescription();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.followerCount = followerCount;
        this.followeeCount = followeeCount;
    }

    @QueryProjection
    public UserInfoResponse(Long userId, String email, String nickname, String profileImageUrl, String introduction, LocalDateTime createdAt, LocalDateTime updatedAt, int followerCount, int followeeCount) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.followerCount = followerCount;
        this.followeeCount = followeeCount;
    }

    public static UserInfoResponse of(User user, int followerCount, int followeeCount) {
        return new UserInfoResponse(user, followerCount, followeeCount);
    }
}
