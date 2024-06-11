package com.server.nodak.domain.vote.service;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteHistory;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.domain.vote.dto.VoteAfterResultResponse;
import com.server.nodak.domain.vote.dto.VoteBeforeResultResponse;
import com.server.nodak.domain.vote.dto.VoteOptionResult;
import com.server.nodak.domain.vote.dto.VoteResponse;
import com.server.nodak.domain.vote.repository.vote.VoteRepository;
import com.server.nodak.domain.vote.repository.votehistory.VoteHistoryRepository;
import com.server.nodak.domain.vote.repository.voteoption.VoteOptionRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.server.nodak.domain.vote.utils.Utils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteService 테스트")
@Slf4j
class VoteServiceTest {
    Random rnd = new Random();

    @InjectMocks
    VoteService voteService;
    @Mock
    UserRepository userRepository;
    @Mock
    VoteRepository voteRepository;
    @Mock
    VoteOptionRepository voteOptionRepository;
    @Mock
    VoteHistoryRepository voteHistoryRepository;

    User user;
    Category category;
    Post post;
    Vote vote;
    VoteOption voteOption;
    VoteHistory voteHistory;

    @BeforeEach
    public void setUp() {
        user = createUser();
        category = Mockito.spy(createCategory());
        post = Mockito.spy(createPost(user, "Post_title", "Post_content", category));
        vote = Mockito.spy(createVote("Vote_title", post));

        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(vote, "id", 1L);
        ReflectionTestUtils.setField(post, "id", 1L);

        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        given(voteRepository.findById(any(Long.class))).willReturn(Optional.of(vote));
    }


    @Test
    void registerVoteOption() {
        // Given
        Long voteId = 1L;
        Integer optionSeq = 1;

        voteOption = createVoteOption(vote, optionSeq, "VoteOption_content");
        voteHistory = createVoteHistory(user, voteOption);

        given(vote.getId()).willReturn(voteId);
        given(voteRepository.findById(voteId)).willReturn(Optional.ofNullable(vote));
        given(voteOptionRepository.findByVoteIdAndSeq(voteId, (long) optionSeq)).willReturn(
                Optional.ofNullable(voteOption));

        // When
        voteService.registerVoteOption(user.getId(), voteId, (long) optionSeq);

        // Then
        then(voteHistoryRepository).should().save(voteHistory);
    }

    // TODO : 새로운 테스트 작성 필요
//    @Test
//    void findVoteResult() {
//        // Given
//        Long voteId = 1L;
//        String voteTitle = "Vote_title";
//        List<VoteOptionResult> voteOptionResults = List.of();
//        boolean isExists = rnd.nextBoolean();
//
//        VoteResponse beforeResponse = VoteBeforeResultResponse.builder().voteId(voteId).voteTitle(voteTitle)
//                .voteOptions(voteOptionResults).build();
//        VoteResponse afterResponse = VoteAfterResultResponse.builder().voteId(voteId).voteTitle(voteTitle)
//                .voteOptions(voteOptionResults).build();
//
//        given(voteRepository.existsHistoryByVoteId(user.getId(), voteId)).willReturn(isExists);
//        if (isExists) {
//            given(voteRepository.findVoteAfter(user.getId(), voteId)).willReturn(afterResponse);
//        } else {
//            given(voteRepository.findVoteBefore(voteId)).willReturn(beforeResponse);
//        }
//
//        // When
//        VoteResponse result = voteService.findVoteResult(user.getId(), voteId);
//
//        // Then
//        if (isExists) {
//            then(voteRepository).should().findVoteAfter(user.getId(), voteId);
//        } else {
//            then(voteRepository).should().findVoteBefore(voteId);
//        }
//        Assertions.assertThat(result.getVoteId()).isEqualTo(voteId);
//        Assertions.assertThat(result.getVoteTitle()).isEqualTo(voteTitle);
//        Assertions.assertThat(result.getVoteOptions().size()).isEqualTo(voteOptionResults.size());
//    }
}