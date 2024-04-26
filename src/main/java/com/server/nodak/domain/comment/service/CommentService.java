package com.server.nodak.domain.comment.service;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.comment.dto.request.CreateCommentRequest;
import com.server.nodak.domain.comment.repository.CommentJpaRepository;
import com.server.nodak.domain.comment.repository.CommentRepository;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.repository.PostJpaRepository;
import com.server.nodak.exception.common.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final PostJpaRepository postJpaRepository;

    public void createComment(long postId, CreateCommentRequest commentRequest) {
        Post post = postJpaRepository.findById(postId).orElseThrow(
                () -> new DataNotFoundException());


        // TODO : 로그인 한 유저 넣기
        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .user(null)
                .post(post)
                .build();

        commentJpaRepository.save(comment);
    }
}
