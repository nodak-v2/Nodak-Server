package com.server.nodak.domain.reply.repository;

import com.server.nodak.domain.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyJpaRepository extends JpaRepository<Reply, Long> {

    @Query("select r from Reply r where r.comment.id = :commentId")
    Optional<Reply> findByCommentId(@Param("commentId") Long commentId);

    @Query("select r from Reply r where r.user.id = :userId")
    List<Reply> getAllByUserId(@Param("userId") Long userId);
}
