package com.server.nodak.domain.vote.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class VoteOptionResult {
    private Long voteOptionId;
    private Integer seq;
    private String voteOptionContent;
    private Integer count;

    @QueryProjection
    public VoteOptionResult(Long voteOptionId, Integer seq, String voteOptionContent, Integer count) {
        this.voteOptionId = voteOptionId;
        this.seq = seq;
        this.voteOptionContent = voteOptionContent;
        this.count = count != null ? count : 0;
    }
}
