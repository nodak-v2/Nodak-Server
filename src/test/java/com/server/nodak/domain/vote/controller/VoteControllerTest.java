package com.server.nodak.domain.vote.controller;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.service.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Random;

import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteController 테스트")
@Slf4j
class VoteControllerTest {

    @InjectMocks
    VoteController voteController;
    @Mock
    VoteService voteService;
    @Mock
    Principal principal;
    MockMvc mockMvc;
    User user;
    Random rnd = new Random();

    @BeforeEach
    public void setUp() {
        user = createUser();

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(voteController)
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);
        lenient().when(principal.getName()).thenReturn(String.valueOf(user.getId()));
    }

    @Test
    @DisplayName("투표결과 조회 테스트")
    void voteResult() throws Exception {
        // Given
        Long voteId = 1L;

        // When
        ResultActions resultActions = mockMvc.perform(get(String.format("/votes/%d", voteId))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("투표결과 조회 테스트 - voteId 값이 문자열이면 예외를 발생시킨다.")
    void voteResultByVoteIdIsString() throws Exception {
        // Given
        String voteId = "voteId";

        // When
        ResultActions resultActions = mockMvc.perform(get(String.format("/votes/%s", voteId)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("투표 등록 테스트")
    void registerVote() throws Exception {
        // Given
        Long voteId = 1L;
        Long optionId = 1L;
        given(principal.getName()).willReturn(String.valueOf(user.getId()));

        // When
        ResultActions resultActions = mockMvc.perform(post(String.format("/votes/%d", voteId))
                .param("option", String.valueOf(optionId))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("투표 등록 테스트 - voteId 값이 문자열이면 예외를 발생시킨다.")
    void registerVoteByVoteIdIsString() throws Exception {
        // Given
        String voteId = "voteId";
        Long optionId = 1L;
        String username = "username";
        given(principal.getName()).willReturn(String.valueOf(user.getId()));

        // When
        ResultActions resultActions = mockMvc.perform(post(String.format("/votes/%s", voteId))
                .param("option", String.valueOf(optionId))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("투표 등록 테스트 - optionId 값이 null이면 예외를 발생시킨다.")
    void registerVoteByOptionIdIsNull() throws Exception {
        // Given
        Long voteId = 1L;
        String username = "username";
        given(principal.getName()).willReturn(username);

        // When
        ResultActions resultActions = mockMvc.perform(post(String.format("/votes/%d", voteId))
                .principal(principal));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }
}