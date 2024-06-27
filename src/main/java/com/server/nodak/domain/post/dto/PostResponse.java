package com.server.nodak.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PostResponse {

    private String author;
    private Boolean isAuthor;
    private Integer commentSize;
    private String profileImageUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    private String content;
    private String imageUrl;
    private Integer starCount;
    private Boolean checkStar;

    @QueryProjection
    public PostResponse(String author, Boolean isAuthor, Integer commentSize, String profileImageUrl,
                        LocalDateTime createdAt,
                        String content,
                        String imageUrl, Integer starCount, Boolean checkStar) {
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
