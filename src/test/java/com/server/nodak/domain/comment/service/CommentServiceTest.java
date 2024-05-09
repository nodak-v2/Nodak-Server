package com.server.nodak.domain.comment.service;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.comment.dto.request.CreateCommentRequest;
import com.server.nodak.domain.comment.dto.request.UpdateCommentRequest;
import com.server.nodak.domain.comment.dto.response.CommentResponse;
import com.server.nodak.domain.comment.repository.CommentRepository;
import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.repository.PostRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    private Long postId;
    private Long commentId;
    private Long userId;
    private Post post;
    private Comment comment;
    private User user;

    @BeforeEach
    void setUp() {
        userId = 1L;
        postId = 1L;
        commentId = 1L;

        user = createUser();

        Category category = new Category("카테고리1");

        post = Post.builder()
                .title("게시글 제목1")
                .content("게시글 본문1")
                .user(user)
                .category(category)
                .build();

        comment = Comment.builder()
                .post(post)
                .user(user)
                .content("Original content")
                .build();

        // ID가 필요한 테스트에서는 ReflectionTestUtils 사용
        ReflectionTestUtils.setField(comment, "id", commentId);
        ReflectionTestUtils.setField(post, "id", postId);
        ReflectionTestUtils.setField(user, "id", 1L);

        lenient().when(postRepository.findById(any(Long.class))).thenReturn(Optional.of(post));
        lenient().when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createComment_test() {
        // given
        CreateCommentRequest createCommentRequest = new CreateCommentRequest("댓글 댓글");

        // when
        commentService.createComment(1L, 1L ,createCommentRequest);

        // then
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("게시글 댓글 조회")
    void fetchCommentsForPost_test() {
        // given
        when(commentRepository.getCommentsByPostId(postId)).thenReturn(Collections.emptyList());

        // when
        List<CommentResponse> result = commentService.fetchCommentsForPost(postId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateComment_test() {
        // given
        UpdateCommentRequest commentRequest = new UpdateCommentRequest("수정된 댓글");
        when(commentRepository.findById(any(Long.class))).thenReturn(Optional.of(comment));

        // when
        commentService.updateComment(postId, userId, commentId, commentRequest);

        // then
        assertEquals("수정된 댓글", comment.getContent());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteComment_test() {
        // given
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        commentService.deleteComment(postId, userId, commentId);

        // then
        verify(commentRepository).delete(comment);
    }
}