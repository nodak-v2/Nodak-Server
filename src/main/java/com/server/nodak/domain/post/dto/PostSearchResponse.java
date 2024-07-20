package com.server.nodak.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.server.nodak.domain.vote.dto.VoteOptionListResult;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchResponse {

    private Long postId;
    private Long voteId;
    private String voteTitle;
    private Integer commentCount;
    private Integer likeCount;
    private Long voterCount;
    private String author;
    private String profileImageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endDate;
    private boolean isTerminated;
    private List<VoteOptionListResult> voteOptions;

    @QueryProjection
    public PostSearchResponse(Long postId, Long voteId, String voteTitle, Integer commentCount,
        Integer likeCount,
        Long voterCount, String author, String profileImageUrl,
        LocalDateTime createdAt, LocalDateTime endDate, boolean isTerminated) {
        this.postId = postId;
        this.voteId = voteId;
        this.voteTitle = voteTitle;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.voterCount = voterCount;
        this.author = author;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.endDate = endDate;
        this.isTerminated = isTerminated;
    }
}
