package com.server.nodak.domain.comment.service;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.comment.dto.request.CreateCommentRequest;
import com.server.nodak.domain.comment.dto.request.UpdateCommentRequest;
import com.server.nodak.domain.comment.dto.response.CommentResponse;
import com.server.nodak.domain.comment.repository.CommentJpaRepository;
import com.server.nodak.domain.comment.repository.CommentRepository;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.repository.PostRepository;
import com.server.nodak.domain.reply.dto.MyCommentHistory;
import com.server.nodak.domain.reply.dto.MyReplyHistory;
import com.server.nodak.domain.reply.entity.Reply;
import com.server.nodak.domain.reply.repository.ReplyRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserHistoryRepository;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.BadRequestException;
import com.server.nodak.exception.common.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final ReplyRepository replyRepository;

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
        List<Comment> comments = commentRepository.findByPostId(postId);

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

        if (comment.getUser().getId() != userId || comment.isDeleted()) {
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

        List<Reply> replyList = replyRepository.findByCommentId(commentId);

        if (replyList.isEmpty()) {
            commentRepository.delete(comment);
        } else {
            comment.setContent("삭제된 댓글입니다.");
            comment.setDeleted(true);
        }
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

    public List<MyCommentHistory> getAllCommentsByUser(long userId) {
        findUser(userId);
        List<Comment> comments = commentRepository.findByUserId(userId);

        List<MyCommentHistory> result = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponse commentResponse = CommentResponse.of(comment);
            result.add(new MyCommentHistory(commentResponse, comment));
        }

        return result;
    }
}
