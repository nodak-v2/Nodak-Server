package com.server.nodak.domain.vote.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class VoteOptionDetailResult {

    private Long voteOptionId;
    private Integer seq;
    private String voteOptionContent;
    private String voteOptionImageUrl;
    private Integer count;

    @QueryProjection
    public VoteOptionDetailResult(Long voteOptionId, Integer seq, String voteOptionContent,
        String voteOptionImageUrl,
        Integer count) {
        this.voteOptionId = voteOptionId;
        this.seq = seq;
        this.voteOptionContent = voteOptionContent;
        this.voteOptionImageUrl = voteOptionImageUrl;
        this.count = count != null ? count : 0;
    }
}
