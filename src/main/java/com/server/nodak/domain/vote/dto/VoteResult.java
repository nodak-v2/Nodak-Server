package com.server.nodak.domain.vote.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResult {

    private Long voteId;
    private String voteTitle;
    private List<VoteOptionDetailResult> voteOptions;
    private Boolean hasVoted;
    private Long voteOptionId;
    private List<Long> voteOptionIds;
    private Long choiceVoteOptionId;
    private Boolean isTerminated;

    @QueryProjection
    public VoteResult(Long voteId, String voteTitle, Long voteOptionId, Boolean isTerminated) {
        this.voteId = voteId;
        this.voteTitle = voteTitle;
        this.hasVoted = false;
        this.choiceVoteOptionId = 0L;
        this.voteOptionId = voteOptionId;
        this.isTerminated = isTerminated;
    }

    @QueryProjection
    public VoteResult(Long voteId, String voteTitle, Long choiceVoteOptionId,
        Long voteOptionId, Boolean isTerminated) {
        this.voteId = voteId;
        this.voteTitle = voteTitle;
        this.hasVoted = true;
        this.choiceVoteOptionId = choiceVoteOptionId;
        this.voteOptionId = voteOptionId;
        this.isTerminated = isTerminated;
    }
}
