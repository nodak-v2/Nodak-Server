package com.server.nodak.domain.vote.repository;

import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.dto.VoteResponse;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long>, VoteRepositoryCustom {
    Optional<Vote> findByPostId(Long postId);

    @Override
    VoteResponse findVoteBefore(Long voteId);

    @Override
    Boolean existsHistoryByVoteId(Long userId, Long voteId);

    @Override
    VoteResponse findVoteAfter(Long userId, Long voteId);
}
