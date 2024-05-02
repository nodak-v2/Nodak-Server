package com.server.nodak.domain.vote.domain;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.createVote;
import static com.server.nodak.domain.vote.utils.Utils.createVoteHistory;
import static com.server.nodak.domain.vote.utils.Utils.createVoteOption;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.global.config.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
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
class VoteHistoryTest {

    @Autowired
    TestEntityManager em;
    User user;
    VoteOption voteOption;
    VoteHistory voteHistory;

    @BeforeEach
    public void setUp() {
        user = createUser();
        Category category = createCategory();
        Post post = createPost(user, "title", "content", category);
        Vote vote = createVote("test_title", post);
        voteOption = createVoteOption(vote, 1, "test_content");
        voteHistory = createVoteHistory(user, voteOption);
        em.persist(user);
        em.persist(category);
        em.persist(post);
        em.persist(vote);
        em.persist(voteOption);
    }

    @Test
    @DisplayName("VoteHistory 저장 테스트")
    public void testSave() {
        // Given
        voteHistory = createVoteHistory(user, voteOption);

        // When
        em.persist(voteHistory);

        //Then
        Assertions.assertThat(em.find(VoteHistory.class, voteHistory.getId())).isEqualTo(voteHistory);
    }

    @Test
    @DisplayName("VoteHistory 저장 테스트 - User가 null이면 예외를 발생시킨다.")
    public void testSaveByUserIsNull() {
        // Given
        voteHistory = createVoteHistory(null, voteOption);

        // When
        Throwable throwable = Assertions.catchThrowable(() -> {
            em.persist(voteHistory);
        });

        //Then
        Assertions.assertThat(throwable).isInstanceOf(ConstraintViolationException.class);
    }
}