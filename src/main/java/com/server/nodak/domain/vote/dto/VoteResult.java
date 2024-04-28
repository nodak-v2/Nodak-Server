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
    private List<VoteOptionResult> voteOptions;
    private Long voteOptionId;
    private List<Long> voteOptionIds;

    @QueryProjection
    public VoteResult(Long voteId, String voteTitle, Long voteOptionId) {
        this.voteId = voteId;
        this.voteTitle = voteTitle;
        this.voteOptionId = voteOptionId;
    }
}
