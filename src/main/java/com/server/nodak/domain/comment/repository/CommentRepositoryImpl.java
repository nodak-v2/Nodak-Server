package com.server.nodak.domain.comment.repository;

import com.querydsl.jpa.JPQLQueryFactory;
import com.server.nodak.domain.comment.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.server.nodak.domain.comment.domain.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final JPQLQueryFactory queryFactory;
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public List<Comment> findByPostId(Long postId) {
        return commentJpaRepository.findByPostId(postId);
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        return commentJpaRepository.findByUserId(userId);
    }

    @Override
    public void save(Comment comment) {
        commentJpaRepository.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        commentJpaRepository.delete(comment);
    }
}
