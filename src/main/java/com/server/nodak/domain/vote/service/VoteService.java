package com.server.nodak.domain.vote.service;

import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.repository.PostRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteHistory;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.domain.vote.dto.VoteResponse;
import com.server.nodak.domain.vote.repository.vote.VoteRepository;
import com.server.nodak.domain.vote.repository.votehistory.VoteHistoryRepository;
import com.server.nodak.domain.vote.repository.voteoption.VoteOptionRepository;
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
    private final PostRepository postRepository;

    @Transactional
    public void registerVoteOption(Long userId, Long voteId, Long optionSeq) {
        User user = findUserById(userId);
        Vote vote = findVoteById(voteId);

        checkIfUserAlreadyVoted(vote.getId(), userId);

        VoteOption voteOption = findVoteOptionByVoteIdAndSeq(vote.getId(), optionSeq);
        VoteHistory voteHistory = createVoteHistory(user, voteOption);

        voteHistoryRepository.save(voteHistory);
    }

    private void checkIfUserAlreadyVoted(Long voteId, Long userId) {
        voteHistoryRepository.findByVoteIdAndUserId(voteId, userId)
                .ifPresent(voteHistory -> {
                    throw new BadRequestException("이미 투표에 참여하였습니다.");
                });
    }

    @Transactional(readOnly = true)
    public VoteResponse findVoteResult(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BadRequestException());
        Long voteId = post.getVote().getId();

        findUserById(userId);
        findVoteById(voteId);

        if (voteRepository.existsHistoryByVoteId(userId, voteId)) {
            return voteRepository.findVoteAfter(userId, voteId);
        }
        return voteRepository.findVoteBefore(voteId);
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

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("use not found"));
    }
}
