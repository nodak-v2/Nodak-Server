package com.server.nodak.domain.follow.repository;

import com.server.nodak.domain.follow.domain.Follow;

import java.util.Optional;

public interface FollowRepository {

    int getUserFollowerCount(Long userId);

    int getUserFolloweeCount(Long userId);

    Optional<Follow> getFollowByRelation(Long followerId, Long followeeId);
}
