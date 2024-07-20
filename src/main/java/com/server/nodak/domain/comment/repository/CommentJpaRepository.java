package com.server.nodak.domain.comment.repository;

import com.server.nodak.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.post.id = :postId")
    List<Comment> findByPostId(@Param("postId") Long postId);

    @Query("select c from Comment c where c.user.id = :userId")
    List<Comment> findByUserId(@Param("userId") Long postId);
}
