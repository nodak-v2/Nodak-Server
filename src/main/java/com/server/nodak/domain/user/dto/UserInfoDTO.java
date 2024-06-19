package com.server.nodak.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import com.server.nodak.domain.user.domain.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoDTO {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String introduction;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;
    private Long postCount;
    private Long voteCount;
    private Long commentCount;
    private Long likeCount;
    private Long followerCount;
    private Long followeeCount;
    private Boolean isFollowing;

    protected UserInfoDTO(User user, Long postCount, Long voteCount, Long commentCount, Long likeCount,
                          Long followerCount) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.postCount = postCount;
        this.voteCount = voteCount;
        this.commentCount = commentCount;
        this.followerCount = followerCount;
        this.likeCount = likeCount;

    }

    protected UserInfoDTO(User user, Long followerCount, Long followeeCount) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.followerCount = followerCount;
        this.followeeCount = followeeCount;
    }

    @QueryProjection
    public UserInfoDTO(Long userId, String email, String nickname, String profileImageUrl,
                       Long postCount, Long voteCount, Long commentCount, Long likeCount,
                       Long followerCount, Long followeeCount, Boolean isFollowing) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.postCount = postCount;
        this.voteCount = voteCount;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.followerCount = followerCount;
        this.followeeCount = followeeCount;
        this.isFollowing = isFollowing;
    }

    @QueryProjection
    public UserInfoDTO(Long userId, String email, String nickname, String profileImageUrl, String introduction,
                       LocalDateTime createdAt, LocalDateTime updatedAt, Long followerCount,
                       Long followeeCount) {
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

    public static UserInfoDTO of(User user, Long postCount, Long voteCount, Long commentCount,
                                 Long likeCount,
                                 Long followerCount) {
        return new UserInfoDTO(user, postCount, voteCount, commentCount, likeCount, followerCount);
    }

    public static UserInfoDTO of(User user, Long followerCount, Long followeeCount) {
        return new UserInfoDTO(user, followerCount, followeeCount);
    }

    public UserInfoResponse toUserInfoResponse(List<PostSearchResponse> userPosts) {
        Map<String, Integer> badge = new HashMap<>();
        badge.put("posting", 1);
        badge.put("voting", 1);
        badge.put("comment", 1);
        badge.put("love", 1);
        badge.put("follower", 1);

        return UserInfoResponse.builder()
                .userId(this.userId)
                .email(this.email)
                .nickname(this.nickname)
                .profileImageUrl(this.profileImageUrl)
                .postCount(this.postCount)
                .followerCount(this.followerCount)
                .followeeCount(this.followeeCount)
                .isFollowing(isFollowing)
                .badge(badge)
                .posts(userPosts)
                .build();
    }
}
