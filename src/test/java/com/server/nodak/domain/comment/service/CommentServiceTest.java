package com.server.nodak.domain.comment.service;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.comment.dto.request.CreateCommentRequest;
import com.server.nodak.domain.comment.dto.request.UpdateCommentRequest;
import com.server.nodak.domain.comment.dto.response.CommentResponse;
import com.server.nodak.domain.comment.repository.CommentJpaRepository;
import com.server.nodak.domain.comment.repository.CommentRepository;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.repository.PostJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private CommentJpaRepository commentJpaRepository;

    @MockBean
    private PostJpaRepository postJpaRepository;

    private Long postId;
    private Long commentId;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        postId = 1L;
        commentId = 1L;

        post = new Post();  // Post 생성자나 빌더로 필요한 초기화 진행
        comment = Comment.builder()
                .post(post)
                .content("Original content")
                .build();

        // ID가 필요한 테스트에서는 ReflectionTestUtils 사용
        ReflectionTestUtils.setField(comment, "id", commentId);
        ReflectionTestUtils.setField(post, "id", postId);
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createComment_테스트() {
        // given
        Long postId = 1L;
        CreateCommentRequest createCommentRequest = new CreateCommentRequest("댓글 댓글");

        Post post = new Post();

        when(postJpaRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentJpaRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        commentService.createComment(postId, createCommentRequest);

        // then
        verify(commentJpaRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("게시글 댓글 조회")
    void fetchCommentsForPost_테스트() {
        // given
        when(postJpaRepository.findById(postId)).thenReturn(Optional.of(new Post()));
        when(commentRepository.getCommentsByPost(postId)).thenReturn(Collections.emptyList());

        // when
        List<CommentResponse> result = commentService.fetchCommentsForPost(postId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateComment_테스트() {
        // given
        UpdateCommentRequest commentRequest = new UpdateCommentRequest("수정된 댓글");

        when(commentJpaRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        commentService.updateComment(postId, commentId, commentRequest);

        // then
        assertEquals("수정된 댓글", comment.getContent());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteComment() {
        // given
        when(commentJpaRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        commentService.deleteComment(postId, commentId);

        // then
        verify(commentJpaRepository).delete(comment);
    }
}