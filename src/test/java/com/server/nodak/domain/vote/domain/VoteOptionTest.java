package com.server.nodak.domain.vote.domain;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.createVote;
import static com.server.nodak.domain.vote.utils.Utils.createVoteOption;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.validation.ConstraintViolationException;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class})
@DisplayName("VoteOption 엔티티 테스트")
@Slf4j
class VoteOptionTest {
    Random rnd = new Random();
    @Autowired
    TestEntityManager em;
    Vote vote;

    @BeforeEach
    public void setUp() {
        User user = createUser();
        Category category = createCategory();
        Post post = createPost(user, "title", "content", category);
        vote = createVote("test_title", post);
        em.persist(category);
        em.persist(vote);
    }

    @Test
    @DisplayName("VoteOption 저장 테스트")
    public void testSave() {
        // Given
        VoteOption voteOption = createVoteOption(vote, null, "test_content");

        // When
        em.persist(voteOption);

        //Then
        Assertions.assertThat(em.find(VoteOption.class, voteOption.getId())).isEqualTo(voteOption);
    }

    @Test
    @DisplayName("VoteOption 저장 테스트 - content가 공백이면 예외를 발생시킨다.")
    public void testSaveByTitleIsEmpty() {
        // Given
        VoteOption voteOption = createVoteOption(vote, null, "");

        // When
        Throwable throwable = Assertions.catchThrowable(() -> {
            em.persist(voteOption);
        });

        //Then
        Assertions.assertThat(throwable).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("VoteOption 저장 테스트 - seq가 0 이하 경우 예외를 발생시킨다.")
    public void testSaveByMinSeq() {
        // Given
        VoteOption voteOption = createVoteOption(vote, -1, "test_content");

        // When
        Throwable throwable = Assertions.catchThrowable(() -> {
            em.persist(voteOption);
        });

        //Then
        Assertions.assertThat(throwable).isInstanceOf(ConstraintViolationException.class);
    }
}