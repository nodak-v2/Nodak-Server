package com.server.nodak.domain.comment.controller;

import com.server.nodak.domain.comment.dto.request.CreateCommentRequest;
import com.server.nodak.domain.comment.dto.request.UpdateCommentRequest;
import com.server.nodak.domain.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentController 테스트")
class CommentControllerTest {

    @InjectMocks
    CommentController commentController;

    @Mock
    CommentService commentService;

    @Mock
    Principal principal;

    MockMvc mockMvc;
    String TEST_URL = "/posts/1/comments";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .build();
    }

    @Test
    @DisplayName("create comment 테스트")
    @WithMockUser(roles = "GENERAL")
    void createCommentTest() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest("New Comment");
        String jsonRequest = "{\"content\":\"New Comment\"}";

        when(principal.getName()).thenReturn("1");
        lenient().doNothing().when(commentService).createComment(eq(1L), eq(1L), any(CreateCommentRequest.class));

        mockMvc.perform(post(TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.body").isEmpty());
    }

    @Test
    @DisplayName("댓글 조회 테스트")
    void getCommentsTest() throws Exception {
        ResultActions result = mockMvc.perform(get(TEST_URL));

        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("update comment 테스트")
    @WithMockUser(roles = "GENERAL")
    void updateCommentTest() throws Exception {
        UpdateCommentRequest updateRequest = new UpdateCommentRequest("Updated Comment");
        String jsonRequest = "{\"content\":\"Updated Comment\"}";

        String userId = String.valueOf(1);
        given(principal.getName()).willReturn(userId);

        mockMvc.perform(patch(TEST_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.body").isEmpty());
    }

    @Test
    @DisplayName("delete comment 테스트")
    @WithMockUser(roles = "GENERAL")
    void deleteCommentTest() throws Exception {
        String userId = String.valueOf(1);
        given(principal.getName()).willReturn(userId);

        mockMvc.perform(delete(TEST_URL + "/1")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.body").isEmpty());
    }
}