package com.server.nodak.domain.follow.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.follow.domain.QFollow;
import com.server.nodak.domain.user.domain.QUser;
import com.server.nodak.domain.user.dto.QUserInfoDTO;
import com.server.nodak.domain.user.dto.UserInfoDTO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository, FollowRepositoryCustom {

    private final FollowJpaRepository followJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public long getUserFollowerCount(Long userId) {
        return Math.toIntExact(redisTemplate.opsForSet().size(userId + ":follower"));
    }

    @Override
    public long getUserFolloweeCount(Long userId) {
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
        Long followeeId = follow.getFollowee().getId();
        Long followerId = follow.getFollower().getId();
        redisTemplate.opsForSet().add(String.valueOf(followerId) + ":followee", followeeId);
        redisTemplate.opsForSet().add(String.valueOf(followeeId) + ":follower", followerId);
        return followJpaRepository.save(follow);
    }

    @Override
    public List<UserInfoDTO> getFolloweesByUserId(Long myId, Long userId) {
        QFollow mainFollow = new QFollow("main");
        QFollow subFollow = new QFollow("sub");

        return queryFactory
            .select(
                new QUserInfoDTO(
                    mainFollow.followee.id,
                    mainFollow.followee.email,
                    mainFollow.followee.nickname,
                    mainFollow.followee.profileImageUrl,
                    mainFollow.followee.description,
                    mainFollow.followee.createdAt,
                    mainFollow.followee.updatedAt,
                    JPAExpressions
                        .select(subFollow.count())
                        .from(subFollow)
                        .where(subFollow.follower.id.eq(mainFollow.followee.id)),
                    JPAExpressions
                        .select(subFollow.count())
                        .from(subFollow)
                        .where(subFollow.followee.id.eq(mainFollow.followee.id)),
                    myId != null ? (
                        JPAExpressions.selectOne()
                            .from(subFollow)
                            .where(subFollow.follower.id.eq(myId)
                                .and(subFollow.followee.id.eq(mainFollow.followee.id)))
                            .where(subFollow.isDeleted.eq(false))
                            .exists()
                    ) : Expressions.FALSE
                )
            )
            .from(mainFollow)
            .where(mainFollow.follower.id.eq(userId))
            .groupBy(mainFollow.followee.id)
            .fetch();
    }

    @Override
    public List<UserInfoDTO> getFollowersByUserId(Long myId, Long userId) {
        QFollow mainFollow = new QFollow("main");
        QFollow subFollow = new QFollow("sub");

        return queryFactory
            .select(
                new QUserInfoDTO(
                    mainFollow.follower.id,
                    mainFollow.follower.email,
                    mainFollow.follower.nickname,
                    mainFollow.follower.profileImageUrl,
                    mainFollow.follower.description,
                    mainFollow.follower.createdAt,
                    mainFollow.follower.updatedAt,
                    JPAExpressions
                        .select(subFollow.count())
                        .from(subFollow)
                        .where(subFollow.follower.id.eq(mainFollow.follower.id)),
                    JPAExpressions
                        .select(subFollow.count())
                        .from(subFollow)
                        .where(subFollow.followee.id.eq(mainFollow.follower.id)),
                    myId != null ? (
                        JPAExpressions.selectOne()
                            .from(subFollow)
                            .where(subFollow.follower.id.eq(myId)
                                .and(subFollow.followee.id.eq(mainFollow.follower.id)))
                            .where(subFollow.isDeleted.eq(false))
                            .exists()
                    ) : Expressions.FALSE
                )
            )
            .from(mainFollow)
            .where(mainFollow.followee.id.eq(userId))
            .groupBy(mainFollow.follower.id)
            .fetch();
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
    public boolean isFollowing(Long followerId, Long followeeId) {
        Boolean isFollow = redisTemplate.opsForSet().isMember(String.valueOf(followeeId) + ":follower", followerId);
        return isFollow != null && isFollow;
    }

    @Override
    public void deleteFromRedis(Long followerId, Long followeeId) {
        redisTemplate.opsForSet().remove(String.valueOf(followerId) + ":followee", followeeId);
        redisTemplate.opsForSet().remove(String.valueOf(followeeId) + ":follower", followerId);
    }

    @Override
    public List<UserInfoDTO> findFollowersByUserId(Long userId) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        return queryFactory
            .select(new QUserInfoDTO(
                user.id,
                user.email,
                user.nickname,
                user.profileImageUrl,
                user.description,
                user.createdAt,
                user.updatedAt,
                follow.countDistinct().longValue().as("followerCount"),
                follow.countDistinct().longValue().as("followeeCount")
            ))
            .from(follow)
            .join(follow.follower, user)
            .where(follow.followee.id.eq(userId).and(follow.isDeleted.isFalse()))
            .groupBy(user.id)
            .fetch();
    }

    @Override
    public List<UserInfoDTO> findFolloweesByUserId(Long userId) {
        QFollow follow = QFollow.follow;
        QUser user = QUser.user;

        return queryFactory
            .select(new QUserInfoDTO(
                user.id,
                user.email,
                user.nickname,
                user.profileImageUrl,
                user.description,
                user.createdAt,
                user.updatedAt,
                follow.countDistinct().longValue().as("followerCount"),
                follow.countDistinct().longValue().as("followeeCount")
            ))
            .from(follow)
            .join(follow.followee, user)
            .where(follow.follower.id.eq(userId).and(follow.isDeleted.isFalse()))
            .groupBy(user.id)
            .fetch();
    }
}
