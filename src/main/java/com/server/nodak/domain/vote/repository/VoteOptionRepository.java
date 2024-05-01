package com.server.nodak.domain.vote.repository;

import com.server.nodak.domain.vote.domain.VoteOption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    Optional<VoteOption> findByVoteIdAndSeq(Long id, Long optionSeq);
}
