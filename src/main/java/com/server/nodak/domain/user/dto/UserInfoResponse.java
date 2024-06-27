package com.server.nodak.domain.user.dto;

import com.server.nodak.domain.post.dto.PostSearchResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private Map<String, Integer> badge = new HashMap<>();

    private List<PostSearchResponse> posts;

}
