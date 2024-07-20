package com.server.nodak.domain.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.reply.dto.ReplyResponse;
import com.server.nodak.domain.reply.entity.Reply;
import com.server.nodak.domain.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;
    private Long userId;
    private String profileImageUrl;
    private String nickname;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    private List<ReplyResponse> children;

    public static CommentResponse of(Comment comment) {
        User user = comment.getUser();
        List<Reply> replies = comment.getReplies();
        List<ReplyResponse> result = new ArrayList<>();

        for (Reply reply : replies) {
            result.add(new ReplyResponse(reply));
        }

        return CommentResponse.builder()
                .commentId(comment.getId())
                .userId(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .children(result)
                .build();
    }
}
