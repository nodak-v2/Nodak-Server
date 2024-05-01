package com.server.nodak.domain.vote.repository;

import com.server.nodak.domain.vote.dto.VoteResultResponse;

public interface VoteRepositoryCustom {
    VoteResultResponse findVoteResult(Long voteId);
}
