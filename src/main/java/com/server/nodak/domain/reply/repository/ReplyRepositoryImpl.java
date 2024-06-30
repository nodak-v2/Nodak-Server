package com.server.nodak.domain.reply.repository;

import com.server.nodak.domain.reply.entity.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepository {

    private final ReplyJpaRepository replyJpaRepository;

    @Override
    public List<Reply> findByCommentId(Long commentId) {
        return replyJpaRepository.findByCommentId(commentId);
    }

    @Override
    public List<Reply> getAllByUserId(Long userId) {
        return replyJpaRepository.getAllByUserId(userId);
    }

    @Override
    public void save(Reply reply) {
        replyJpaRepository.save(reply);
    }

    @Override
    public void delete(Reply reply) {
        replyJpaRepository.delete(reply);
    }

    @Override
    public Optional<Reply> findById(Long replyId) {
        return replyJpaRepository.findById(replyId);
    }
}
