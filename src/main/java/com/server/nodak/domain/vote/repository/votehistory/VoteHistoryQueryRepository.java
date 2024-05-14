package com.server.nodak.domain.vote.repository.votehistory;

import com.server.nodak.domain.vote.domain.VoteHistory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteHistoryQueryRepository {

    Optional<VoteHistory> findByVoteIdAndUserId(long voteId, long userId);
}
