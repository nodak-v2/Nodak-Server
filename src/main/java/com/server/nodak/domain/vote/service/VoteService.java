package com.server.nodak.domain.vote.service;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteHistory;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.domain.vote.dto.VoteResultResponse;
import com.server.nodak.domain.vote.repository.VoteHistoryRepository;
import com.server.nodak.domain.vote.repository.VoteOptionRepository;
import com.server.nodak.domain.vote.repository.VoteRepository;
import com.server.nodak.exception.common.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteHistoryRepository voteHistoryRepository;

    @Transactional
    public void registerVoteOption(String email, Long voteId, Long optionSeq) {
        User user = findUserByEmail(email);
        Vote vote = findVoteById(voteId);
        VoteOption voteOption = findVoteOptionByVoteIdAndSeq(vote.getId(), optionSeq);
        VoteHistory voteHistory = createVoteHistory(user, voteOption);
        voteHistoryRepository.save(voteHistory);
    }

    @Transactional(readOnly = true)
    public VoteResultResponse findVoteResult(Long voteId) {
        return voteRepository.findVoteResult(voteId);
    }

    private VoteHistory createVoteHistory(User user, VoteOption voteOption) {
        return VoteHistory.builder().user(user).voteOption(voteOption).build();
    }

    private Vote findVoteById(Long voteId) {
        return voteRepository.findById(voteId).orElseThrow(() -> new BadRequestException("존재하지 않는 투표입니다."));
    }

    private VoteOption findVoteOptionByVoteIdAndSeq(Long voteId, Long optionSeq) {
        return voteOptionRepository.findByVoteIdAndSeq(voteId, optionSeq)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 투표 후보지입니다."));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException());
    }

}
