package com.server.nodak.domain.vote.controller;

import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteController 테스트")
@Slf4j
class VoteControllerTest {
    @InjectMocks
    VoteController voteController;
    @Mock
    VoteService voteService;
    MockMvc mockMvc;
    User user;

    @BeforeEach
    public void setUp() {
        user = createUser();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(voteController)
                .build();
    }
     
    @Test
    @DisplayName("투표결과 조회 테스트")
    void voteResult() throws Exception {
        // Given
        Long voteId = 1L;
        mockMvc.perform(get(String.format("/votes/%d", voteId)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("투표결과 조회 테스트 - voteId 값이 문자열이면 예외를 발생시킨다.")
    void voteResultByVoteIdIsString() throws Exception {
        // Given
        String voteId = "voteId";
        mockMvc.perform(get(String.format("/votes/%s", voteId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerVote() {
    }
}