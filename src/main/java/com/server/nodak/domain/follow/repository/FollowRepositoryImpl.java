package com.server.nodak.domain.follow.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.follow.domain.QFollow;
import com.server.nodak.domain.user.domain.QUser;
import com.server.nodak.domain.user.domain.User;
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
    public List<User> getFollowersByUserId(Long userId) {
        return followJpaRepository.findFollowersByUserId(userId);
    }

    @Override
    public List<User> getFolloweesByUserId(Long userId) {
        return followJpaRepository.findFolloweesByUserId(userId);
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
