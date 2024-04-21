package com.server.nodak.domain.vote.utils;

import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteOption;
import java.util.Random;

public class Utils {
    static Random rnd = new Random();

    public static VoteOption createVoteOption(Vote vote, Integer seq, String content) {
        return VoteOption.builder()
                .vote(vote)
                .content(content)
                .seq((seq == null) ? rnd.nextInt(1, 10) : seq)
                .build();
    }

    public static Vote createVote(String title) {
        return Vote.builder()
                .title(title)
                .build();
    }
}
