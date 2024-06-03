package com.server.nodak.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PostSearchResponse {
    private Long postId;
    private Long voteId;
    private String title;
    private Integer commentCount;
    private Integer likeCount;
    private Long voterCount;
    private String author;
    private String profileImageUrl;
    private String postImageUrl;

    @QueryProjection
    public PostSearchResponse(Long postId, Long voteId, String title, Integer commentCount, Integer likeCount,
                              Long voterCount, String author, String profileImageUrl, String postImageUrl) {
        this.postId = postId;
        this.voteId = voteId;
        this.title = title;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.voterCount = voterCount;
        this.author = author;
        this.profileImageUrl = profileImageUrl;
        this.postImageUrl = postImageUrl;
    }
}
