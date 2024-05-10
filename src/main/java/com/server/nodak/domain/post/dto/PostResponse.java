package com.server.nodak.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PostResponse {

    private String title;
    private String author;
    private Boolean isAuthor;
    private Integer commentSize;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private String content;
    private String imageUrl;
    private Integer starCount;
    private Boolean checkStar;

    @QueryProjection
    public PostResponse(String title, String author, Boolean isAuthor, Integer commentSize, String profileImageUrl,
                        LocalDateTime createdAt,
                        String content,
                        String imageUrl, Integer starCount, Boolean checkStar) {
        this.title = title;
        this.author = author;
        this.isAuthor = isAuthor;
        this.commentSize = commentSize;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.content = content;
        this.imageUrl = imageUrl;
        this.starCount = starCount;
        this.checkStar = checkStar;
    }
}
