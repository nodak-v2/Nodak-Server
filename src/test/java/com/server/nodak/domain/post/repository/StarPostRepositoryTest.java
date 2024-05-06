package com.server.nodak.domain.post.repository;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.domain.StarPost;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import java.util.UUID;
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
@DisplayName("StarPostRepository 테스트")
@Slf4j
class StarPostRepositoryTest {

    @Autowired
    StarPostRepository starPostRepository;
    @Autowired
    EntityManager em;
    User user;
    Category category;
    Post post;

    @BeforeEach
    public void setUp() {
        user = createUser();
        category = createCategory();
        post = createPost(user, randomUUID(1, 10), randomUUID(1, 10), category);
        em.persist(user);
        em.persist(category);
        em.persist(post);
    }

    @Test
    @DisplayName("save 테스트")
    public void saveTest() {
        // Given
        StarPost starPost = StarPost.builder().user(user).post(post).build();

        // When
        StarPost saveStarPost = starPostRepository.save(starPost);

        // Then
        Assertions.assertThat(starPost.getId()).isEqualTo(saveStarPost.getId());
        Assertions.assertThat(starPost.getUser().getId()).isEqualTo(user.getId());
        Assertions.assertThat(starPost.getPost().getId()).isEqualTo(post.getId());
    }

    @Test
    @DisplayName("existsByUserIdAndPostId 테스트")
    public void existsByUserIdAndPostIdTest() {
        // Given
        StarPost starPost = StarPost.builder().user(user).post(post).build();
        starPostRepository.save(starPost);

        // When
        boolean result = starPostRepository.existsByUserIdAndPostId(user.getId(), post.getId());

        // Then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("delete 테스트")
    public void deleteTest() {
        // Given
        StarPost starPost = StarPost.builder().user(user).post(post).build();
        starPostRepository.save(starPost);

        // When
        StarPost findStarPost = starPostRepository.findById(starPost.getId()).get();
        findStarPost.delete(true);
        starPostRepository.save(findStarPost);

        // Then
        Assertions.assertThat(findStarPost.isDeleted()).isEqualTo(true);
        Assertions.assertThat(starPostRepository.existsById(findStarPost.getId())).isFalse();
    }

    public String randomUUID(int start, int end) {
        return UUID.randomUUID().toString().substring(start, end);
    }

}