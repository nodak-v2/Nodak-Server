package com.server.nodak.domain.follow.repository;

import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.UserInfoDTO;
import java.util.List;
import java.util.Optional;

public interface FollowRepository {

    long getUserFollowerCount(Long userId);

    long getUserFolloweeCount(Long userId);

    Optional<Follow> getFollowByRelation(Long followerId, Long followeeId);

    Optional<Follow> checkIfDeletedFollowExists(Long followerId, Long followeeId);

    Follow save(Follow follow);

    List<UserInfoDTO> getFollowersByUserId(Long myId, Long userId);

    List<UserInfoDTO> getFolloweesByUserId(Long myId, Long userId);

    List<Long> getFollowerIds(Long userId);

    boolean isFollowing(Long followerId, Long followeeId);

    void deleteFromRedis(Long followerId, Long followeeId);
}
