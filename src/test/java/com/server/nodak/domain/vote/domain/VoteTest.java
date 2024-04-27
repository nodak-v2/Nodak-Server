package com.server.nodak.domain.vote.domain;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.createVote;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.validation.ConstraintViolationException;
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
@DisplayName("Vote 엔티티 테스트")
@Slf4j
class VoteTest {
    @Autowired
    TestEntityManager em;

    Post post;

    Vote vote;

    @BeforeEach
    public void setUp() {
        User user = createUser();
        Category category = createCategory();
        post = createPost(user, "title", "content", category);
        em.persist(user);
        em.persist(category);
        em.persist(post);
    }

    @Test
    @DisplayName("Vote 저장 테스트")
    public void testSave() {
        // Given
        vote = createVote("test_title", post);

        // When
        em.persist(vote);

        //Then
        Assertions.assertThat(em.find(Vote.class, vote.getId())).isEqualTo(vote);
    }

    @Test
    @DisplayName("Vote 저장 테스트 - title이 공백이면 예외를 발생시킨다.")
    public void testSaveByTitleIsEmpty() {
        // Given
        vote = createVote("", post);

        // When
        Throwable throwable = Assertions.catchThrowable(() -> {
            em.persist(vote);
        });

        //Then
        Assertions.assertThat(throwable).isInstanceOf(ConstraintViolationException.class);
    }
}