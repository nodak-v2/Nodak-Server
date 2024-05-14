package com.server.nodak.domain.vote.repository;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteHistory;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.domain.vote.repository.votehistory.VoteHistoryRepository;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.IntStream;

import static com.server.nodak.domain.vote.utils.Utils.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class})
@DisplayName("VoteHistoryRepository 테스트")
class VoteHistoryRepositoryTest {

    @Autowired
    VoteHistoryRepository voteHistoryRepository;
    @Autowired
    EntityManager em;
    User user;
    Post post;
    Vote vote;
    VoteOption voteOption;
    VoteHistory voteHistory;

    @BeforeEach
    public void setUp() {
        user = createUser();
        Category category = createCategory();
        post = createPost(user, "title", "content", category);
        vote = createVote("test_title", post);
        voteOption = createVoteOption(vote, 1, "test_content");
        voteHistory = createVoteHistory(user, voteOption);
        em.persist(user);
        em.persist(category);
        em.persist(post);
        em.persist(vote);
        em.persist(voteOption);
    }

    @Test
    @DisplayName("save 테스트")
    public void saveTest() {
        // Given
        VoteHistory voteHistory = createVoteHistory(user, voteOption);

        // When
        VoteHistory saveVoteHistory = voteHistoryRepository.save(voteHistory);

        // Then
        Assertions.assertThat(saveVoteHistory.getId()).isEqualTo(voteHistory.getId());
    }

    @Test
    @DisplayName("saveAll 테스트")
    public void saveAllTest() {
        // Given
        VoteOption option1 = createVoteOption(vote, 1, "test_content1");
        VoteOption option2 = createVoteOption(vote, 1, "test_content2");
        VoteOption option3 = createVoteOption(vote, 1, "test_content3");

        List<VoteHistory> voteHistorys = List.of(createVoteHistory(user, option1),
                createVoteHistory(user, option2), createVoteHistory(user, option3));

        // When
        List<VoteHistory> saveVoteHistories = voteHistoryRepository.saveAll(voteHistorys);

        // Then
        IntStream.range(0, saveVoteHistories.size()).forEach(i -> {
            Assertions.assertThat(saveVoteHistories.get(i).getId()).isEqualTo(saveVoteHistories.get(i).getId());
        });
    }

    @Test
    @DisplayName("findById 테스트")
    public void findByIdTest() {
        // Given
        VoteHistory voteHistory = createVoteHistory(user, voteOption);
        VoteHistory saveVoteHistory = voteHistoryRepository.save(voteHistory);

        // When
        VoteHistory findVoteHistory = voteHistoryRepository.findById(saveVoteHistory.getId()).get();

        // Then
        Assertions.assertThat(findVoteHistory.getId()).isEqualTo(saveVoteHistory.getId());
    }
}