package com.server.nodak.domain.vote.repository.vote;

import static com.server.nodak.domain.vote.domain.QVote.vote;
import static com.server.nodak.domain.vote.domain.QVoteHistory.voteHistory;
import static com.server.nodak.domain.vote.domain.QVoteOption.voteOption;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.vote.domain.QVoteOption;
import com.server.nodak.domain.vote.dto.QVoteOptionDetailResult;
import com.server.nodak.domain.vote.dto.QVoteResult;
import com.server.nodak.domain.vote.dto.VoteAfterResultResponse;
import com.server.nodak.domain.vote.dto.VoteBeforeResultResponse;
import com.server.nodak.domain.vote.dto.VoteOptionDetailResult;
import com.server.nodak.domain.vote.dto.VoteResponse;
import com.server.nodak.domain.vote.dto.VoteResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class VoteRepositoryImpl implements VoteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public VoteResponse findVoteAfter(Long userId, Long voteId) {

        List<VoteResult> voteResults = queryFactory.select(
                new QVoteResult(
                    vote.id,
                    vote.title,
                    JPAExpressions
                        .select(voteHistory.voteOption.id)
                        .from(voteHistory)
                        .where(voteHistory.voteOption.in(vote.voteOptions),
                            voteHistory.user.id.eq(userId))
                        .limit(1),
                    voteOption.id
                )
            )
            .from(vote)
            .innerJoin(voteOption)
            .on(voteOption.vote.id.eq(vote.id))
            .where(vote.id.eq(voteId))
            .fetch();

        List<Long> voteOptionIds = mapToVoteOptionIds(voteResults);

        QVoteOption subVoteOption = new QVoteOption("subVoteOption");

        List<VoteOptionDetailResult> voteOptionResults = queryFactory.select(
                new QVoteOptionDetailResult(
                    subVoteOption.id, subVoteOption.seq, subVoteOption.content, subVoteOption.imageUrl,
                    subVoteOption.voteHistories.size()
                )
            )
            .from(subVoteOption)
            .groupBy(subVoteOption.id)
            .having(subVoteOption.id.in(voteOptionIds))
            .fetch();

        VoteResponse voteAfterResultResponse = toAfterResultResponseDto(voteResults,
            voteOptionResults);

        return voteAfterResultResponse;
    }

    @Override
    public VoteResponse findVoteBefore(Long voteId) {

        List<VoteResult> voteResults = queryFactory.select(
                new QVoteResult(vote.id, vote.title, voteOption.id)
            )
            .from(vote)
            .innerJoin(voteOption)
            .on(vote.id.eq(voteOption.vote.id))
            .where(vote.id.eq(voteId))
            .fetch();

        List<Long> voteOptionIds = mapToVoteOptionIds(voteResults);

        QVoteOption subVoteOption = new QVoteOption("subVoteOption");

        List<VoteOptionDetailResult> voteOptionResults = queryFactory.select(
                new QVoteOptionDetailResult(
                    subVoteOption.id, subVoteOption.seq, subVoteOption.content, subVoteOption.imageUrl,
                    subVoteOption.voteHistories.size()
                )
            )
            .from(subVoteOption)
            .groupBy(subVoteOption.id)
            .having(subVoteOption.id.in(voteOptionIds))
            .fetch();

        VoteResponse voteBeforeResultResponse = toBeforeResultResponseDto(voteResults,
            voteOptionResults);

        return voteBeforeResultResponse;
    }

    private List<Long> mapToVoteOptionIds(List<VoteResult> voteResults) {
        return voteResults.stream().map(e -> e.getVoteOptionId()).toList();
    }

    @Override
    public Boolean existsHistoryByVoteId(Long userId, Long voteId) {

        Integer fetchOne = queryFactory.selectOne()
            .from(voteOption)
            .innerJoin(voteHistory)
            .on(voteOption.id.eq(voteHistory.voteOption.id))
            .where(
                voteOption.vote.id.eq(voteId),
                voteHistory.user.id.eq(userId)
            )
            .fetchFirst();

        return fetchOne != null;
    }

    private VoteResponse toAfterResultResponseDto(List<VoteResult> voteResults,
        List<VoteOptionDetailResult> voteOptionResults) {
        return VoteAfterResultResponse.builder()
            .voteId(voteResults.get(0).getVoteId())
            .voteTitle(voteResults.get(0).getVoteTitle())
            .hasVoted(voteResults.get(0).getHasVoted())
            .choiceVoteOptionId(voteResults.get(0).getChoiceVoteOptionId())
            .totalNumber(
                voteOptionResults.stream().map(e -> e.getCount()).reduce((x, y) -> x + y).get())
            .voteOptions(voteOptionResults)
            .build();
    }

    private VoteResponse toBeforeResultResponseDto(List<VoteResult> voteResults,
        List<VoteOptionDetailResult> voteOptionResults) {
        return VoteBeforeResultResponse.builder()
            .voteId(voteResults.get(0).getVoteId())
            .voteTitle(voteResults.get(0).getVoteTitle())
            .hasVoted(voteResults.get(0).getHasVoted())
            .choiceVoteOptionId(voteResults.get(0).getChoiceVoteOptionId())
            .totalNumber(
                voteOptionResults.stream().map(e -> e.getCount()).reduce((x, y) -> x + y).get())
            .voteOptions(voteOptionResults)
            .build();
    }
}
