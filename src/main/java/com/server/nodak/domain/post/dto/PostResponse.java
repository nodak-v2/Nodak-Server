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
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private String content;
    private String imageUrl;
    private Integer starCount;
    private Boolean checkStar;

    @QueryProjection
    public PostResponse(String title, String author, String profileImageUrl, LocalDateTime createdAt, String content,
                        String imageUrl, Integer starCount, Boolean checkStar) {
        this.title = title;
        this.author = author;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.content = content;
        this.imageUrl = imageUrl;
        this.starCount = starCount;
        this.checkStar = checkStar;
    }
}
