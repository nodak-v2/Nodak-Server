package com.server.nodak.domain.vote.repository.votehistory;

import com.querydsl.jpa.JPQLQueryFactory;
import com.server.nodak.domain.vote.domain.VoteHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.server.nodak.domain.vote.domain.QVoteHistory.voteHistory;

@Repository
@RequiredArgsConstructor
public class VoteHistoryRepositoryImpl implements VoteHistoryQueryRepository {

    private final JPQLQueryFactory queryFactory;

    @Override
    public Optional<VoteHistory> findByVoteIdAndUserId(long voteId, long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(voteHistory)
                        .where(voteHistory.voteOption.vote.id.eq(voteId)
                        .and(voteHistory.user.id.eq(userId)))
                        .fetchFirst());
    }
}
