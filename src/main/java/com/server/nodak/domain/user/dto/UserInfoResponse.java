package com.server.nodak.domain.user.dto;

import com.server.nodak.domain.post.dto.PostSearchResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

    private Long userId;

    private String email;

    private String nickname;

    private String profileImageUrl;

    private Long postCount;

    private Long followerCount;

    private Long followeeCount;

    private Boolean isFollowing;

    private BadgeResponse badge;

    private List<PostSearchResponse> posts;

}
