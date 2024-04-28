package com.server.nodak.domain.vote.repository;

import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.dto.VoteResultResponse;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long>, VoteRepositoryCustom {
    Optional<Vote> findByPostId(Long postId);

    @Override
    VoteResultResponse findVoteResult(Long voteId);
}
