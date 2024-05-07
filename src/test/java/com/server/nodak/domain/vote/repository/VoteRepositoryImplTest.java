package com.server.nodak.domain.vote.repository;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.createVote;
import static com.server.nodak.domain.vote.utils.Utils.createVoteHistory;
import static com.server.nodak.domain.vote.utils.Utils.createVoteOption;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteHistory;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.domain.vote.dto.VoteResponse;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class})
@DisplayName("VoteRepositoryImpl 테스트")
@Slf4j
class VoteRepositoryImplTest {

    @Autowired
    EntityManager em;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    VoteOptionRepository voteOptionRepository;
    @Autowired
    VoteHistoryRepository voteHistoryRepository;
    PageRequest pageRequest;
    User user;
    Category category1;
    Category category2;
    List<Post> posts = new ArrayList<>();
    HashMap<String, List<VoteOption>> voteOptionMap = new HashMap<>();
    List<VoteHistory> voteHistories = new ArrayList<>();
    List<Long> voteOptionIdsByUserId = new ArrayList<>();
    Random rnd = new Random();
    int postCount;
    int voteOptionCount;
    int voteHistoryCount;
    int selectId;

    @BeforeEach
    public void setUp() {
        pageRequest = PageRequest.of(0, 10);
        user = createUser();
        category1 = createCategory("운동");
        category2 = createCategory("연애");
        em.persist(user);
        em.persist(category1);
        em.persist(category2);

        postCount = rnd.nextInt(3, 5) + 1;
        voteOptionCount = rnd.nextInt(3, 10);
        voteHistoryCount = rnd.nextInt(1, voteOptionCount);
    }

    @Test
    @DisplayName("투표 후 결과 조회 테스트")
    void findVoteAfter() {
        // Given
        saveVoteAndVoteOptions(postCount, voteOptionCount, voteHistoryCount);
        selectId = rnd.nextInt(postCount);

        // When
        VoteResponse result = voteRepository.findVoteAfter(user.getId(), posts.get(selectId).getVote().getId());

        // When
        Assertions.assertThat(result.getVoteOptions().size()).isEqualTo(voteOptionCount);
        Assertions.assertThat(result.getHasVoted()).isTrue();
        Assertions.assertThat(result.getTotalNumber()).isEqualTo(voteHistoryCount);
        Assertions.assertThat(result.getVoteId()).isEqualTo(posts.get(selectId).getVote().getId());
        Assertions.assertThat(voteOptionIdsByUserId.contains(result.getChoiceVoteOptionId()));
        Assertions.assertThat(result.getVoteTitle()).isEqualTo(posts.get(selectId).getVote().getTitle());
    }

    @Test
    @DisplayName("투표 전 결과 조회 테스트")
    public void findVoteBeforeTest() {
        // Given
        saveVoteAndVoteOptions(postCount, voteOptionCount, voteHistoryCount);
        selectId = rnd.nextInt(postCount);

        // When
        VoteResponse result = voteRepository.findVoteBefore(posts.get(selectId).getVote().getId());

        // Then
        Assertions.assertThat(result.getVoteOptions().size()).isEqualTo(voteOptionCount);
        Assertions.assertThat(result.getHasVoted()).isFalse();
        Assertions.assertThat(result.getTotalNumber()).isEqualTo(voteHistoryCount);
        Assertions.assertThat(result.getVoteId()).isEqualTo(posts.get(selectId).getVote().getId());
        Assertions.assertThat(result.getChoiceVoteOptionId()).isEqualTo(0);
        Assertions.assertThat(result.getVoteTitle()).isEqualTo(posts.get(selectId).getVote().getTitle());
    }

    @Test
    @DisplayName("투표한 유저는 true값을 반환한다.")
    void existsHistoryByVoted() {
        // Given
        saveVoteAndVoteOptions(postCount, voteOptionCount, voteHistoryCount);
        selectId = rnd.nextInt(postCount);

        // When
        Boolean isExists = voteRepository.existsHistoryByVoteId(user.getId(), posts.get(selectId).getVote().getId());

        // Then
        Assertions.assertThat(isExists).isTrue();
    }

    @Test
    @DisplayName("투표하지 않은 유저는 false값을 반환한다.")
    void existsHistoryByNotVoted() {
        // Given
        User newUser = createUser();
        em.persist(newUser);
        saveVoteAndVoteOptions(postCount, voteOptionCount, voteHistoryCount);
        selectId = rnd.nextInt(postCount);

        // When
        Boolean isExists = voteRepository.existsHistoryByVoteId(newUser.getId(), posts.get(selectId).getVote().getId());

        // Then
        Assertions.assertThat(isExists).isFalse();
    }

    private void saveVoteAndVoteOptions(int postCount, int voteOptionCount, int voteHistoryCount) {
        IntStream.range(0, postCount).forEach(idx -> {
            String uuid = randomUUID(1, 4);
            Post post = createPost(user, uuid, uuid, category1);
            posts.add(post);
            Vote vote = createVote(String.format("Vote_title_%s", uuid), post);
            voteOptionMap.put(post.getTitle(), createVoteOptions(vote, voteOptionCount));
        });

        voteOptionMap.values().stream().forEach(voteOptions -> {

            List<VoteHistory> historyList = IntStream.rangeClosed(1, voteHistoryCount).mapToObj(e -> {
                if (e == 1) {
                    VoteHistory voteHistory = createVoteHistory(user, voteOptions.get(rnd.nextInt(voteOptions.size())));
                    voteOptionIdsByUserId.add(voteHistory.getVoteOption().getId());
                    return voteHistory;
                }

                User tmpUser = createUser();
                em.persist(tmpUser);

                return createVoteHistory(tmpUser, voteOptions.get(rnd.nextInt(voteOptions.size())));
            }).toList();

            voteHistories.addAll(historyList);
        });

        voteHistoryRepository.saveAll(voteHistories);
    }

    private List<VoteOption> createVoteOptions(Vote vote, int size) {
        return IntStream.rangeClosed(1, size).mapToObj(e ->
                createVoteOption(vote, e, String.format("VoteOption_content_%d", e))
        ).collect(Collectors.toList());
    }

    public String randomUUID(int start, int end) {
        return UUID.randomUUID().toString().substring(start, end);
    }
}