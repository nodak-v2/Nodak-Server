package com.server.nodak.domain.follow.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.follow.domain.QFollow;
import com.server.nodak.domain.user.domain.QUser;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.QUserInfoResponse;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository, FollowRepositoryCustom {

    private final FollowJpaRepository followJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public int getUserFollowerCount(Long userId) {
        return Math.toIntExact(redisTemplate.opsForSet().size(userId + ":follower"));
    }

    @Override
    public int getUserFolloweeCount(Long userId) {
        return Math.toIntExact(redisTemplate.opsForSet().size(userId + ":followee"));
    }

    @Override
    public Optional<Follow> getFollowByRelation(Long followerId, Long followeeId) {
        return followJpaRepository.getFollowByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public Optional<Follow> checkIfDeletedFollowExists(Long followerId, Long followeeId) {
        return followJpaRepository.checkIfDeletedFollowExists(followerId, followeeId);
    }

    @Override
    public Follow save(Follow follow) {
        return followJpaRepository.save(follow);
    }
    
    @Override
    public List<User> getFollowersByUserId(Long userId) {
        return followJpaRepository.findFollowersByUserId(userId);
    }

    @Override
    public List<Long> getFollowerIds(Long userId) {
        Set<Object> followers = redisTemplate.opsForSet().members(userId + ":follower");

        if (followers == null) {
            return Collections.emptyList();
        }
        return followers.stream()
                .map(Object::toString)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFolloweesByUserId(Long userId) {
        return followJpaRepository.findFolloweesByUserId(userId);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followeeId) {
        Boolean isFollow = redisTemplate.opsForSet().isMember(String.valueOf(followerId), followeeId);
        return isFollow != null && isFollow;
    }

    @Override
    public List<UserInfoResponse> findFollowersByUserId(Long userId) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        return queryFactory
                .select(new QUserInfoResponse(
                        user.id,
                        user.email,
                        user.nickname,
                        user.profileImageUrl,
                        user.description,
                        user.createdAt,
                        user.updatedAt,
                        follow.countDistinct().intValue().as("followerCount"),
                        follow.countDistinct().intValue().as("followeeCount")
                ))
                .from(follow)
                .join(follow.follower, user)
                .where(follow.followee.id.eq(userId).and(follow.isDeleted.isFalse()))
                .groupBy(user.id)
                .fetch();
    }

    @Override
    public List<UserInfoResponse> findFolloweesByUserId(Long userId) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        return queryFactory
                .select(new QUserInfoResponse(
                        user.id,
                        user.email,
                        user.nickname,
                        user.profileImageUrl,
                        user.description,
                        user.createdAt,
                        user.updatedAt,
                        follow.countDistinct().intValue().as("followerCount"),
                        follow.countDistinct().intValue().as("followeeCount")
                ))
                .from(follow)
                .join(follow.followee, user)
                .where(follow.follower.id.eq(userId).and(follow.isDeleted.isFalse()))
                .groupBy(user.id)
                .fetch();
    }
}
