package com.server.nodak.domain.comment.dto.response;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;
    private String profileImageUrl;
    private String writerName;
    private String content;
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
