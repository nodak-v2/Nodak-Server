package com.server.nodak.domain.vote.repository.vote;

import com.server.nodak.domain.vote.dto.VoteResponse;

public interface VoteRepositoryCustom {
    VoteResponse findVoteBefore(Long voteId);

    Boolean existsHistoryByVoteId(Long userId, Long voteId);

    VoteResponse findVoteAfter(Long userId, Long voteId);
}
