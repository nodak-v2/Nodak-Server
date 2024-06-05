package com.server.nodak.domain.follow.repository;

import com.server.nodak.domain.follow.domain.Follow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository{
    private final FollowJpaRepository followJpaRepository;

    @Override
    public int getUserFollowerCount(Long userId) {
        return followJpaRepository.getFollowerCount(userId);
    }

    @Override
    public int getUserFolloweeCount(Long userId) {
        return followJpaRepository.getFolloweeCount(userId);
    }

    @Override
    public Optional<Follow> getFollowByRelation(Long followerId, Long followeeId) {
        return followJpaRepository.getFollowByFollowerIdAndFolloweeId(followerId, followeeId);
    }
}
