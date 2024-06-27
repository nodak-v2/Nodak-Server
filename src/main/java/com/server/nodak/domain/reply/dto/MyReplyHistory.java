package com.server.nodak.domain.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyReplyHistory {

    private Long postId;
    private Long replyId;
    private Long userId;
    private String profileImageUrl;
    private String nickname;

    @Setter
    private String content;

    public MyReplyHistory(ReplyResponse replyResponse, Long postId) {
        this.postId = postId;
        this.replyId = replyResponse.getReplyId();
        this.userId = replyResponse.getUserId();
        this.profileImageUrl = replyResponse.getProfileImageUrl();
        this.nickname = replyResponse.getContent();
        this.content = replyResponse.getContent();
    }
}
