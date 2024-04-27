package com.server.nodak.domain.comment.repository;

import com.querydsl.jpa.JPQLQueryFactory;
import com.server.nodak.domain.comment.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.server.nodak.domain.comment.domain.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final JPQLQueryFactory queryFactory;

    public List<Comment> getCommentsByPost(long postId) {
        return queryFactory
                .selectFrom(comment)
                .where(comment.post.id.eq(postId)
                        .and(comment.isDeleted.eq(false)))
                .fetch();
    }
}
