package com.server.nodak.domain.vote.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.vote.domain.QVote;
import com.server.nodak.domain.vote.domain.QVoteHistory;
import com.server.nodak.domain.vote.domain.QVoteOption;
import com.server.nodak.domain.vote.dto.QVoteOptionResult;
import com.server.nodak.domain.vote.dto.QVoteResult;
import com.server.nodak.domain.vote.dto.VoteOptionResult;
import com.server.nodak.domain.vote.dto.VoteResult;
import com.server.nodak.domain.vote.dto.VoteResultResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class VoteRepositoryImpl implements VoteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public VoteResultResponse findVoteResult(Long voteId) {
        QVote vote = QVote.vote;
        QVoteOption voteOption = QVoteOption.voteOption;
        QVoteHistory voteHistory = QVoteHistory.voteHistory;

        List<VoteResult> voteResults = queryFactory.select(
                        new QVoteResult(vote.id, vote.title, voteOption.id)
                )
                .from(vote)
                .join(voteOption)
                .on(vote.id.eq(voteOption.vote.id))
                .where(vote.id.eq(voteId))
                .fetch();
        List<Long> voteOptionIds = voteResults.stream().map(e -> e.getVoteOptionId()).toList();

        List<VoteOptionResult> voteOptionResults = queryFactory.select(
                        new QVoteOptionResult(
                                voteOption.id, voteOption.seq, voteOption.content, voteOption.voteHistories.size()
                        )
                )
                .from(voteOption)
                .innerJoin(voteHistory)
                .on(voteOption.id.eq(voteHistory.voteOption.id))
                .groupBy(voteOption.id)
                .having(voteOption.id.in(voteOptionIds))
                .fetch();

        VoteResultResponse voteResultResponse = changeResponseDto(voteResults, voteOptionResults);

        return voteResultResponse;
    }

    private VoteResultResponse changeResponseDto(List<VoteResult> voteResults,
                                                 List<VoteOptionResult> voteOptionResults) {
        return VoteResultResponse.builder()
                .voteId(voteResults.get(0).getVoteId())
                .voteTitle(voteResults.get(0).getVoteTitle())
                .voteOptions(voteOptionResults)
                .build();
    }
}
