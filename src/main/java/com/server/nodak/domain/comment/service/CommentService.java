package com.server.nodak.domain.comment.service;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.comment.dto.request.CreateCommentRequest;
import com.server.nodak.domain.comment.dto.request.UpdateCommentRequest;
import com.server.nodak.domain.comment.dto.response.CommentResponse;
import com.server.nodak.domain.comment.repository.CommentJpaRepository;
import com.server.nodak.domain.comment.repository.CommentRepository;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.repository.PostJpaRepository;
import com.server.nodak.exception.common.BadRequestException;
import com.server.nodak.exception.common.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final PostJpaRepository postJpaRepository;

    @Transactional
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

    @Transactional(readOnly = true)
    public List<CommentResponse> fetchCommentsForPost(long postId) {
        postJpaRepository.findById(postId).orElseThrow(
                () -> new DataNotFoundException());

        List<Comment> comments = commentRepository.getCommentsByPost(postId);

        return convertToCommentResponseList(comments);
    }

    private List<CommentResponse> convertToCommentResponseList(List<Comment> comments) {
        List<CommentResponse> result = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponse commentResponse = CommentResponse.of(comment);
            result.add(commentResponse);
        }
        return result;
    }

    @Transactional
    public void updateComment(long postId, long commentId, UpdateCommentRequest commentRequest) {
        Comment comment = getComment(commentId);

        validateCommentBelongsToPost(postId, comment);
        comment.setContent(commentRequest.getContent());
    }

    private Comment getComment(long commentId) {
        return commentJpaRepository.findById(commentId).orElseThrow(
                () -> new BadRequestException()
        );
    }

    private void validateCommentBelongsToPost(long postId, Comment comment) {
        if (comment.getPost().getId() != postId) {
            throw new BadRequestException();
        }
    }

    @Transactional
    public void deleteComment(long postId, long commentId) {
        Comment comment = getComment(commentId);
        validateCommentBelongsToPost(postId, comment);

        // TODO : 해당 댓글의 유저의 것인지 검사하는 로직 필요
        commentJpaRepository.delete(comment);
    }
}
