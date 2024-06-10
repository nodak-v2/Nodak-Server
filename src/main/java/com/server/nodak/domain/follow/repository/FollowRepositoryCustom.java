package com.server.nodak.domain.follow.repository;

import com.server.nodak.domain.user.dto.UserInfoResponse;

import java.util.List;

public interface FollowRepositoryCustom {
    List<UserInfoResponse> findFollowersByUserId(Long userId);
    List<UserInfoResponse> findFolloweesByUserId(Long userId);
}
