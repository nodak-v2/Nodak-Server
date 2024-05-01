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
import com.server.nodak.domain.vote.dto.VoteResultResponse;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
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
    User user;
    Vote vote;
    Category category;
    Random rnd = new Random();

    @BeforeEach
    public void setUp() {
        user = createUser();
        category = createCategory();
        em.persist(user);
        em.persist(category);
    }

    @Test
    @DisplayName("투표 결과 조회 테스트")
    public void findVoteResultTest() {
        // Given
        int voteOptionCount = rnd.nextInt(1, 10);
        int voteHistoryCount = rnd.nextInt(1, 10);
        saveVoteAndVoteOptions(voteOptionCount, voteHistoryCount);
        Vote findVote = voteRepository.findById(vote.getId()).get();

        // When
        VoteResultResponse voteResult = voteRepository.findVoteResult(findVote.getId());

        // Then
        Assertions.assertThat(voteResult.getVoteOptions().size()).isEqualTo(voteOptionCount);
        Assertions.assertThat(voteResult.getVoteOptions().get(0).getCount()).isEqualTo(voteHistoryCount);
        Assertions.assertThat(voteResult.getTotalNumber()).isEqualTo(voteOptionCount * voteHistoryCount);
    }

    private void saveVoteAndVoteOptions(int voteOptionCount, int voteHistoryCount) {
        String uuid = UUID.randomUUID().toString().substring(1, 9);
        Post post = createPost(user, String.format("Post_title_%s", uuid), String.format("Post_content_%s", uuid),
                category);
        vote = createVote(String.format("Vote_title_%s", uuid), post);
        List<VoteOption> voteOptions = createVoteOptions(vote, voteOptionCount);
        voteOptionRepository.saveAll(voteOptions);
        voteOptions.stream().forEach(voteOption -> {
            voteHistoryRepository.saveAll(createVoteHistorys(user, voteOption, voteHistoryCount));
        });

    }

    private List<VoteHistory> createVoteHistorys(User user, VoteOption voteOption, int size) {
        return IntStream.rangeClosed(1, size).mapToObj(e ->
                createVoteHistory(user, voteOption)
        ).collect(Collectors.toList());
    }

    private List<VoteOption> createVoteOptions(Vote vote, int size) {
        return IntStream.rangeClosed(1, size).mapToObj(e ->
                createVoteOption(vote, e, String.format("VoteOption_content_%d", e))
        ).collect(Collectors.toList());
    }
}