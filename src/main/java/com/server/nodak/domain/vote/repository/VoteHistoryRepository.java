package com.server.nodak.domain.vote.repository;

import com.server.nodak.domain.vote.domain.VoteHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteHistoryRepository extends JpaRepository<VoteHistory, Long> {
}
