package com.server.nodak.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PostSearchResponse {

    private Long postId;
    private Long voteId;
    private Integer commentCount;
    private Integer likeCount;
    private Long voterCount;
    private String author;
    private String profileImageUrl;
    private String postImageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endDate;
    private boolean isTerminated;

    @Nullable
    private List<String> voteOptions;

    @QueryProjection
    public PostSearchResponse(Long postId, Long voteId, Integer commentCount, Integer likeCount,
        Long voterCount, String author, String profileImageUrl, String postImageUrl,
        LocalDateTime createdAt, LocalDateTime endDate,
        boolean isTerminated, List<String> voteOptions) {
        this.postId = postId;
        this.voteId = voteId;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.voterCount = voterCount;
        this.author = author;
        this.profileImageUrl = profileImageUrl;
        this.postImageUrl = postImageUrl;
        this.createdAt = createdAt;
        this.endDate = endDate;
        this.isTerminated = isTerminated;
        this.voteOptions = voteOptions;
    }
}
