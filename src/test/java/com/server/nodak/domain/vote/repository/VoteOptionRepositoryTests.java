package com.server.nodak.domain.vote.repository;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.createVote;
import static com.server.nodak.domain.vote.utils.Utils.createVoteOption;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import java.util.List;
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
@DisplayName("VoteOptionRepository 테스트")
@Slf4j
class VoteOptionRepositoryTests {

    @Autowired
    VoteOptionRepository voteOptionRepository;
    @Autowired
    EntityManager em;
    User user;
    Category category;
    Post post;
    Vote vote;

    @BeforeEach
    public void setUp() {
        user = createUser();
        category = createCategory();
        em.persist(user);
        em.persist(category);
        post = createPost(user, "title", "content", category);
        vote = createVote("title", post);
    }

    @Test
    @DisplayName("save 테스트")
    public void saveTest() {
        // Given
        VoteOption voteOption = createVoteOption(vote, 1, "content");

        // When
        VoteOption saveVoteOption = voteOptionRepository.save(voteOption);

        // Then
        Assertions.assertThat(saveVoteOption).isNotNull();
    }

    @Test
    @DisplayName("saveAll 테스트")
    public void saveAllTest() {
        // Given
        List<VoteOption> voteOptions = List.of(createVoteOption(vote, 1, "content1"),
                createVoteOption(vote, 2, "content2"),
                createVoteOption(vote, 3, "content3"));

        // When
        List<VoteOption> saveVoteOptions = voteOptionRepository.saveAll(voteOptions);

        // Then
        IntStream.range(0, saveVoteOptions.size()).forEach(i -> {
            Assertions.assertThat(saveVoteOptions.get(i).getId()).isEqualTo(saveVoteOptions.get(i).getId());
        });
    }

    @Test
    @DisplayName("findById 테스트")
    public void findByIdTest() {
        // Given
        VoteOption voteOption = createVoteOption(vote, 1, "content");
        VoteOption saveVoteOption = voteOptionRepository.save(voteOption);

        // When
        VoteOption findVoteOption = voteOptionRepository.findById(saveVoteOption.getId()).get();

        // Then
        Assertions.assertThat(findVoteOption.getId()).isEqualTo(saveVoteOption.getId());
    }

    @Test
    @DisplayName("findByVoteIdAndSeq 테스트")
    public void findByVoteIdAndSeqTest() {
        // Given
        List<VoteOption> voteOptions = List.of(createVoteOption(vote, 1, "content1"),
                createVoteOption(vote, 2, "content2"),
                createVoteOption(vote, 3, "content3"));
        List<VoteOption> saveVoteOptions = voteOptionRepository.saveAll(voteOptions);

        // When
        VoteOption findVoteOption1 = voteOptionRepository.findByVoteIdAndSeq(vote.getId(), 1L).get();
        VoteOption findVoteOption2 = voteOptionRepository.findByVoteIdAndSeq(vote.getId(), 2L).get();
        VoteOption findVoteOption3 = voteOptionRepository.findByVoteIdAndSeq(vote.getId(), 3L).get();

        // Then
        Assertions.assertThat(findVoteOption1.getContent()).isEqualTo(voteOptions.get(0).getContent());
        Assertions.assertThat(findVoteOption2.getContent()).isEqualTo(voteOptions.get(1).getContent());
        Assertions.assertThat(findVoteOption3.getContent()).isEqualTo(voteOptions.get(2).getContent());
    }

}