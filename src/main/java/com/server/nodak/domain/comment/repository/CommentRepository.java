package com.server.nodak.domain.comment.repository;

import com.server.nodak.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository {

    List<Comment> findByPostId(Long postId);
    List<Comment> findByUserId(Long userId);
    void save(Comment comment);
    void delete(Comment comment);
}
