package com.server.nodak.domain.comment.service;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.comment.dto.request.CreateCommentRequest;
import com.server.nodak.domain.comment.dto.request.UpdateCommentRequest;
import com.server.nodak.domain.comment.dto.response.CommentResponse;
import com.server.nodak.domain.comment.repository.CommentRepository;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.repository.PostRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.BadRequestException;
import com.server.nodak.exception.common.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createComment(long postId, long userId, CreateCommentRequest commentRequest) {
        Post post = findPost(postId);
        User user = findUser(userId);

        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .user(user)
                .post(post)
                .build();

        commentRepository.save(comment);
    }

    private Post findPost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new DataNotFoundException());
        return post;
    }

    private User findUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new DataNotFoundException("user not found"));
        return user;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> fetchCommentsForPost(long postId) {
        findPost(postId);
        List<Comment> comments = commentRepository.getCommentsByPostId(postId);

        return convertToCommentResponseList(comments);
    }

    private List<CommentResponse> convertToCommentResponseList(List<Comment> comments) {
        return comments.stream()
                .map(CommentResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(long postId, long userId, long commentId, UpdateCommentRequest commentRequest) {
        findUser(userId);
        Comment comment = getComment(commentId);

        if (comment.getUser().getId() != userId) {
            throw new BadRequestException();
        }

        validateCommentBelongsToPost(postId, comment);
        comment.setContent(commentRequest.getContent());
    }

    @Transactional
    public void deleteComment(long postId, long userId, long commentId) {
        findUser(userId);
        Comment comment = getComment(commentId);
        validateCommentBelongsToPost(postId, comment);

        if (comment.getUser().getId() != userId) {
            throw new BadRequestException();
        }
        commentRepository.delete(comment);
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new BadRequestException()
        );
    }

    private void validateCommentBelongsToPost(long postId, Comment comment) {
        if (comment.getPost().getId() != postId) {
            throw new BadRequestException();
        }
    }
}
