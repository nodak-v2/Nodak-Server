package com.server.nodak.domain.reply.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.nodak.domain.reply.entity.Reply;
import com.server.nodak.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponse {

    private Long replyId;
    private Long userId;
    private String profileImageUrl;
    private String nickname;

    @Setter
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    public ReplyResponse(Reply reply) {
        User user = reply.getUser();
        this.replyId = reply.getId();
        this.userId = user.getId();
        this.profileImageUrl = user.getProfileImageUrl();
        this.nickname = user.getNickname();

        if (reply.isDeleted()) {
            this.content = "삭제된 댓글입니다.";
        } else {
            this.content = reply.getContent();
        }
        this.createdAt = reply.getCreatedAt();
    }
}
