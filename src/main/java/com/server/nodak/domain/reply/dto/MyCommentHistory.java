package com.server.nodak.domain.reply.dto;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.comment.dto.response.CommentResponse;
import com.server.nodak.domain.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentHistory {

    private Long postId;
    private Long commentId;
    private Long userId;
    private String profileImageUrl;
    private String nickname;
    private String content;

    public MyCommentHistory(CommentResponse commentResponse, Comment comment) {
        Post post = comment.getPost();
        this.postId = post.getId();
        this.commentId = commentResponse.getCommentId();
        this.userId = commentResponse.getUserId();
        this.profileImageUrl = commentResponse.getProfileImageUrl();
        this.nickname = commentResponse.getNickname();
        this.content = commentResponse.getContent();
    }
}
