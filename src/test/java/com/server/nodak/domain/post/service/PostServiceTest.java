package com.server.nodak.domain.post.service;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createPostRequest;
import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.domain.StarPost;
import com.server.nodak.domain.post.dto.PostRequest;
import com.server.nodak.domain.post.dto.PostResponse;
import com.server.nodak.domain.post.dto.PostSearchRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import com.server.nodak.domain.post.repository.CategoryRepository;
import com.server.nodak.domain.post.repository.PostRepository;
import com.server.nodak.domain.post.repository.StarPostRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.LongStream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 테스트")
@Slf4j
class PostServiceTest {
    Random rnd = new Random();

    @InjectMocks
    PostService postService;
    @Mock
    UserRepository userRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    StarPostRepository starPostRepository;
    User user;
    Category category;

    @BeforeEach
    public void setUp() {
        user = Mockito.spy(createUser());
        category = Mockito.spy(createCategory("카테고리"));
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    void savePost() {
        // Given
        PostRequest postRequest = createPostRequest("Post_title", category.getTitle(), "Post_content", "Vote_title",
                "http://image.com", createVoteOptionMap());
        given(userRepository.findById(user.getId())).willReturn(Optional.ofNullable(user));
        given(categoryRepository.findByTitle(postRequest.getChannel())).willReturn(Optional.ofNullable(category));

        // When
        postService.savePost(user.getId(), postRequest);

        // Then
        then(postRepository).should().save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 검색(조회) 테스트")
    public void findPostBySearch() {
        // Given
        Long categoryId = rnd.nextLong(1, 10);
        Long count = rnd.nextLong(1, 10);
        PageRequest pageRequest = PageRequest.of(0, 10);
        PostSearchRequest searchRequest = PostSearchRequest.builder().categoryId(categoryId).build();
        List<PostSearchResponse> postSearchResponses = createPostSearchResponses(10);
        given(postRepository.search(searchRequest, pageRequest)).willReturn(
                new PageImpl<>(postSearchResponses, pageRequest, count));

        // When
        Page<PostSearchResponse> result = postService.findPostBySearch(searchRequest, pageRequest);

        // Then
        Assertions.assertThat(result.getContent()).isEqualTo(postSearchResponses);
    }

    @Test
    @DisplayName("게시글 단건 조회 테스트")
    public void findPostOne() {
        // Given
        Long postId = rnd.nextLong(1, 10);
        PostResponse postResponse = Mockito.spy(PostResponse.builder()
                .title("게시글 제목")
                .author("작성자")
                .profileImageUrl("http://프로필이미지")
                .createdAt(LocalDateTime.now())
                .content("게시글 내용")
                .imageUrl("http://게시글이미지")
                .starCount(3)
                .checkStar(true)
                .build());
        given(postRepository.findOne(user.getId(), postId)).willReturn(postResponse);

        // When
        PostResponse response = postService.findPostOne(user.getId(), postId);

        // Then
        Assertions.assertThat(response.getTitle()).isEqualTo(postResponse.getTitle());
        Assertions.assertThat(response.getAuthor()).isEqualTo(postResponse.getAuthor());
        Assertions.assertThat(response.getCreatedAt()).isEqualTo(postResponse.getCreatedAt());
        Assertions.assertThat(response.getContent()).isEqualTo(postResponse.getContent());
        Assertions.assertThat(response.getImageUrl()).isEqualTo(postResponse.getImageUrl());
        Assertions.assertThat(response.getStarCount()).isEqualTo(postResponse.getStarCount());
        Assertions.assertThat(response.getCheckStar()).isEqualTo(postResponse.getCheckStar());
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    public void updatePost() {
        // Given
        Post post = createPost(user, randomUUID(), randomUUID(), category);
        PostRequest postRequest = createPostRequest("Post_title", category.getTitle(), "Post_content", "Vote_title",
                "http://image.com", createVoteOptionMap());
        given(postRepository.findByIdAndUserId(post.getId(), user.getId())).willReturn(Optional.of(post));

        // When
        postService.updatePost(user.getId(), post.getId(), postRequest);
        post.update(postRequest);

        // Then
        then(postRepository).should().save(post);
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    public void removePost() {
        // Given
        Post post = Mockito.spy(createPost(user, randomUUID(), randomUUID(), category));
        given(postRepository.findByIdAndUserId(post.getId(), user.getId())).willReturn(Optional.of(post));

        // When
        postService.removePost(user.getId(), post.getId());

        // Then
        then(post).should().delete(true);
        then(postRepository).should().save(post);
        Assertions.assertThat(post.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("좋아요 등록 테스트")
    public void registerLikeTest() {
        // Given
        Post post = createPost(user, randomUUID(), randomUUID(), category);
        StarPost starPost = StarPost.builder().user(user).post(post).build();

        given(userRepository.findById(user.getId())).willReturn(Optional.ofNullable(user));
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        // When
        postService.registerLike(user.getId(), post.getId());

        // Then
        then(starPostRepository).should().save(starPost);
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    public void cancleLikeTest() {
        // Given
        Post post = createPost(user, randomUUID(), randomUUID(), category);
        StarPost starPost = Mockito.spy(StarPost.builder().user(user).post(post).build());

        given(starPostRepository.findByDeletedIsTrue(user.getId(), post.getId())).willReturn(
                Optional.ofNullable(starPost));

        // When
        postService.cancleLike(user.getId(), post.getId());

        // Then
        then(starPost).should().delete(true);
        then(starPostRepository).should().save(starPost);
        Assertions.assertThat(starPost.isDeleted()).isTrue();
    }

    public List<PostSearchResponse> createPostSearchResponses(int size) {
        return LongStream.rangeClosed(1, size)
                .mapToObj(e -> PostSearchResponse.builder().postId(e)
                        .postImageUrl(String.format("http://postImage%d.com", e))
                        .author(String.format("작성자%d", e))
                        .profileImageUrl(String.format("http://profileImage%d.com", e)).totalCount(10L).build())
                .toList();
    }

    public Map<Integer, String> createVoteOptionMap() {
        return Map.of(
                1, "voteOption1",
                2, "voteOption2",
                3, "voteOption3"
        );
    }
}