package com.server.nodak.domain.follow.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.follow.domain.QFollow;
import com.server.nodak.domain.user.domain.QUser;
import com.server.nodak.domain.user.dto.QUserInfoDTO;
import com.server.nodak.domain.user.dto.UserInfoDTO;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository, FollowRepositoryCustom {

    private final FollowJpaRepository followJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public long getUserFollowerCount(Long userId) {
        return followJpaRepository.getFollowerCount(userId);
    }

    @Override
    public long getUserFolloweeCount(Long userId) {
        return followJpaRepository.getFolloweeCount(userId);
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
    public List<UserInfoDTO> getFollowersByUserId(Long myId, Long userId) {
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
    public List<UserInfoDTO> getFolloweesByUserId(Long myId, Long userId) {
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
