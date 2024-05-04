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
    private Long totalCount;
    private String author;
    private String profileImageUrl;
    private String postImageUrl;

    @QueryProjection
    public PostSearchResponse(Long postId, Long voteId, String title, Long totalCount, String author,
                              String profileImageUrl,
                              String postImageUrl) {
        this.postId = postId;
        this.voteId = voteId;
        this.title = title;
        this.totalCount = totalCount;
        this.author = author;
        this.profileImageUrl = profileImageUrl;
        this.postImageUrl = postImageUrl;
    }
}
