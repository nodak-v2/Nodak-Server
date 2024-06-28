package com.server.nodak.domain.user.repository;

import static com.server.nodak.domain.comment.domain.QComment.comment;
import static com.server.nodak.domain.follow.domain.QFollow.follow;
import static com.server.nodak.domain.post.domain.QStarPost.starPost;
import static com.server.nodak.domain.user.domain.QUser.user;
import static com.server.nodak.domain.vote.domain.QVoteHistory.voteHistory;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQueryFactory;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.QUserInfoDTO;
import com.server.nodak.domain.user.dto.UserInfoDTO;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JPQLQueryFactory queryFactory;

    private final UserJpaRepository userJpaRepository;

    @Override
    public long count() {
        return userJpaRepository.count();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return userJpaRepository.findByNickname(nickname);
    }

    @Override
    public Optional<UserInfoDTO> getUserInfo(Long userId, Long myId) {
        UserInfoDTO fetch = queryFactory.select(
                new QUserInfoDTO(
                    user.id,
                    user.email,
                    user.nickname,
                    user.profileImageUrl,
                    user.posts.size().longValue(),
                    JPAExpressions
                        .select(voteHistory.count())
                        .from(voteHistory)
                        .where(voteHistory.user.id.eq(userId)),
                    JPAExpressions
                        .select(comment.count())
                        .from(comment)
                        .where(comment.user.id.in(userId))
                        .where(comment.isDeleted.eq(false)),
                    JPAExpressions
                        .select(starPost.count())
                        .from(starPost)
                        .where(starPost.user.id.in(userId))
                        .where(starPost.isDeleted.eq(false)),
                    JPAExpressions
                        .select(follow.count())
                        .from(follow)
                        .where(follow.follower.id.in(userId))
                        .where(follow.isDeleted.eq(false)),
                    JPAExpressions
                        .select(follow.count())
                        .from(follow)
                        .where(follow.followee.id.in(userId))
                        .where(follow.isDeleted.eq(false)),
                    myId != null ? (
                        JPAExpressions.selectOne()
                            .from(follow)
                            .where(follow.follower.id.eq(myId).and(follow.followee.id.eq(userId)))
                            .where(follow.isDeleted.eq(false))
                            .exists()
                    ) : Expressions.FALSE
                )
            )
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne();
        return Optional.ofNullable(fetch);
    }
}
