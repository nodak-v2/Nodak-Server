package com.server.nodak.domain.vote.utils;

import static com.server.nodak.domain.user.domain.UserProvider.KAKAO;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.dto.PostRequest;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteHistory;
import com.server.nodak.domain.vote.domain.VoteOption;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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
        return User.createUser(randomUUID(), randomUUID(), randomUUID(), KAKAO);
    }

    public static VoteHistory createVoteHistory(User user, VoteOption voteOption) {
        return VoteHistory.builder().user(user).voteOption(voteOption).build();
    }

    public static Category createCategory() {
        return new Category("축구");
    }

    public static Category createCategory(String title) {
        return new Category(title);
    }

    public static PostRequest createPostRequest(String title, String channel, String postContent, String voteTitle,
                                                String imageUrl, Map<Integer, String> voteOption) {
        return PostRequest.builder()
                .title(title)
                .channel(channel)
                .content(postContent)
                .voteTitle(voteTitle)
                .imageUrl(imageUrl)
                .voteOptionContent(voteOption)
                .build();
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString().substring(1, 10);
    }
}
