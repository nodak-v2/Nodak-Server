package com.server.nodak.domain.vote.repository;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.createVote;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
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
@DisplayName("VoteRepository 테스트")
@Slf4j
class VoteRepositoryTest {

    @Autowired
    VoteRepository voteRepository;
    @Autowired
    EntityManager em;
    User user;
    Category category;
    Post post;

    @BeforeEach
    public void setUp() {
        user = createUser();
        category = createCategory();
        em.persist(user);
        post = createPost(user, "title", "content", category);
    }

    @Test
    @DisplayName("save 테스트")
    public void saveTest() {
        // Given
        Vote vote = createVote("test_title", post);

        // When
        Vote saveVote = voteRepository.save(vote);

        // Then
        Assertions.assertThat(saveVote).isNotNull();
    }

    @Test
    @DisplayName("findById 테스트")
    public void findByIdTest() {
        // Given
        Vote vote = createVote("test_title", post);
        Vote saveVote = voteRepository.save(vote);

        // When
        Vote findVote = voteRepository.findById(saveVote.getId()).get();

        // Then
        Assertions.assertThat(findVote.getId()).isEqualTo(saveVote.getId());
    }

    @Test
    @DisplayName("findByPostId 테스트")
    public void findByPostIdTest() {
        // Given
        Post post1 = createPost(user, "post1_title", "post1_content", category);
        Post post2 = createPost(user, "post2_title", "post2_content", category);
        Vote vote1 = createVote("post1번 투표", post1);
        Vote vote2 = createVote("post2번 투표", post2);
        voteRepository.save(vote1);
        voteRepository.save(vote2);

        // When
        Vote post1Vote = voteRepository.findByPostId(post1.getId()).get();
        Vote post2Vote = voteRepository.findByPostId(post2.getId()).get();

        // Then
        Assertions.assertThat(post1Vote.getId()).isEqualTo(vote1.getId());
        Assertions.assertThat(post2Vote.getId()).isEqualTo(vote2.getId());
    }


}