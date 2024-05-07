package com.server.nodak.domain.post.controller;

import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.server.nodak.domain.post.dto.PostRequest;
import com.server.nodak.domain.post.service.PostService;
import com.server.nodak.domain.user.domain.User;
import java.security.Principal;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostController 테스트")
@Slf4j
class PostControllerTest {

    @InjectMocks
    PostController postController;
    @Mock
    Principal principal;
    @Mock
    PostService postService;
    MockMvc mockMvc;
    User user;
    PageRequest pageRequest;
    Random rnd = new Random();

    @BeforeEach
    public void setUp() {
        pageRequest = PageRequest.of(0, 10);
        user = createUser();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("게시글 리스트 조회 테스트")
    void postSearch() throws Exception {
        // Given

        // When
        ResultActions resultActions = mockMvc.perform(get("/posts/search"));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 단건 조회 테스트")
    void postDetails() throws Exception {
        // Given
        Long postId = rnd.nextLong(10);
        String userId = String.valueOf(rnd.nextLong(10));
        given(principal.getName()).willReturn(userId);

        // When
        ResultActions resultActions = mockMvc.perform(get(String.format("/posts/%d", postId))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void registerPost() throws Exception {
        // Given
        String userId = String.valueOf(rnd.nextLong(10));
        PostRequest postRequest = PostRequest.builder()
                .title("Post_title")
                .content("Post_content")
                .voteTitle("Vote_title")
                .imageUrl("http://image.com")
                .channel("운동")
                .build();
        given(principal.getName()).willReturn(userId);

        // When
        ResultActions resultActions = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postRequestByJson(postRequest))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void updatePost() throws Exception {
        // Given
        Long postId = rnd.nextLong(10);
        String userId = String.valueOf(rnd.nextLong(10));
        PostRequest postRequest = PostRequest.builder()
                .title("Post_title")
                .content("Post_content")
                .voteTitle("Vote_title")
                .imageUrl("http://image.com")
                .channel("운동")
                .build();
        given(principal.getName()).willReturn(userId);

        // When
        ResultActions resultActions = mockMvc.perform(patch(String.format("/posts/%d", postId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(postRequestByJson(postRequest))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void registerLike() throws Exception {
        // Given
        Long postId = rnd.nextLong(10);
        String userId = String.valueOf(rnd.nextLong(10));
        given(principal.getName()).willReturn(userId);

        // When
        ResultActions resultActions = mockMvc.perform(post(String.format("/posts/%d/stars", postId))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void cancleLike() throws Exception {
        // Given
        Long postId = rnd.nextLong(10);
        String userId = String.valueOf(rnd.nextLong(10));
        given(principal.getName()).willReturn(userId);

        // When
        ResultActions resultActions = mockMvc.perform(delete(String.format("/posts/%d/stars", postId))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void deletePost() throws Exception {
        // Given
        Long postId = rnd.nextLong(10);
        String userId = String.valueOf(rnd.nextLong(10));
        given(principal.getName()).willReturn(userId);

        // When
        ResultActions resultActions = mockMvc.perform(delete(String.format("/posts/%d", postId))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isOk());
    }

    public String postRequestByJson(PostRequest req) {
        return String.format(
                "{\"title\" : \"%s\", \"content\" : \"%s\", \"imageUrl\" : \"%s\","
                        + " \"channel\" : \"%s\", \"voteTitle\" : \"%s\", \"voteOptionContent\" : { \"%d\": \"%s\"} }"
                , req.getTitle(), req.getContent(), req.getImageUrl(), req.getVoteTitle(), req.getChannel(),
                0,
                "투표 옵션");
    }
}