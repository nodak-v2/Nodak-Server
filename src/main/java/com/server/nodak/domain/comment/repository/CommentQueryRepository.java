package com.server.nodak.domain.comment.repository;

import com.server.nodak.domain.comment.domain.Comment;

import java.util.List;

public interface CommentQueryRepository {
    List<Comment> getCommentsByPostId(long postId);
}
