package com.server.nodak.domain.vote.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponse {
    private Long voteId;

    private String voteTitle;

    private Boolean hasVoted;

    private Long choiceVoteOptionId;

    private int totalNumber;

    private List<VoteOptionResult> voteOptions;
}
