package com.server.nodak.domain.vote.repository.voteoption;

import com.server.nodak.domain.vote.domain.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    Optional<VoteOption> findByVoteIdAndSeq(Long id, Long optionSeq);
}
