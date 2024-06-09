package com.server.nodak.domain.follow.repository;

import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface FollowRepository {

    int getUserFollowerCount(Long userId);

    int getUserFolloweeCount(Long userId);

    Optional<Follow> getFollowByRelation(Long followerId, Long followeeId);

    Follow save(Follow follow);

    List<User> getFollowersByUserId(Long userId);

    List<User> getFolloweesByUserId(Long userId);
}
