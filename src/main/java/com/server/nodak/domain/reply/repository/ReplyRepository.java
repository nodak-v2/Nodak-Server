package com.server.nodak.domain.reply.repository;

import com.server.nodak.domain.post.repository.PostRepositoryImpl;
import com.server.nodak.domain.reply.entity.Reply;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository {

    List<Reply> findByCommentId(Long commentId);
    List<Reply> getAllByUserId(Long userId);
    void save(Reply reply);
    void delete(Reply reply);
    Optional<Reply> findById(Long replyId);
}
