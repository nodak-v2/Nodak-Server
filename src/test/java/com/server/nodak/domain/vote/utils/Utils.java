package com.server.nodak.domain.vote.utils;

import static com.server.nodak.domain.user.domain.UserProvider.KAKAO;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteHistory;
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

    public static Vote createVote(String title, Post post) {
        return Vote.builder()
                .title(title)
                .post(post)
                .build();
    }

    public static Post createPost(User user, String title, String content, Category category) {
        return Post.builder()
                .title(title)
                .content(content)
                .imageUrl("abc.abc")
                .user(user)
                .category(category)
                .build();
    }

    public static User createUser() {
        return User.createUser("user1@email.com", "pw123", "닉네임1", KAKAO);
    }

    public static VoteHistory createVoteHistory(User user, VoteOption voteOption) {
        return VoteHistory.builder().user(user).voteOption(voteOption).build();
    }

    public static Category createCategory() {
        return new Category("축구");
    }
}
