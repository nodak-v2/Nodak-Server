package com.server.nodak.domain.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;
    private String profileImageUrl;
    private String writerName;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    public static CommentResponse of(Comment comment) {
        User user = comment.getUser();

        // TODO : profileImageUrl, writerName 수정
        return CommentResponse.builder()
                .commentId(comment.getId())
                .profileImageUrl(null)
                .writerName("닉네임 1")
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
