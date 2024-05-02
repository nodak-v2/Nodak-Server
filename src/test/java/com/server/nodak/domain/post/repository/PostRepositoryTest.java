package com.server.nodak.domain.post.repository;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
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
@DisplayName("PostRepository 테스트")
@Slf4j
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    EntityManager em;
    User user;
    Category category;

    @BeforeEach
    public void setUp() {
        user = createUser();
        category = createCategory();
        em.persist(user);
        em.persist(category);
    }

    @Test
    @DisplayName("save 테스트")
    public void saveTest() {
        // Given
        Post post = createPost(user, String.format("title_%s", randomUUID()), String.format("content_%s", randomUUID()),
                category);

        // When
        Post savePost = postRepository.save(post);

        // Then
        Assertions.assertThat(savePost.getId()).isNotNull();
    }

    @Test
    @DisplayName("saveAll 테스트")
    public void saveAllTest() {
        // Given
        List<Post> posts = List.of(
                createPost(user, String.format("title_%s", randomUUID()), String.format("content_%s", randomUUID()),
                        category),
                createPost(user, String.format("title_%s", randomUUID()), String.format("content_%s", randomUUID()),
                        category),
                createPost(user, String.format("title_%s", randomUUID()), String.format("content_%s", randomUUID()),
                        category)
        );

        // When
        List<Post> savePosts = postRepository.saveAll(posts);

        // Then
        IntStream.range(0, savePosts.size()).forEach(i -> {
            Assertions.assertThat(posts.get(i).getId()).isEqualTo(savePosts.get(i).getId());
        });
    }

    @Test
    @DisplayName("findById 테스트")
    public void findByIdTest() {
        // Given
        Post post = createPost(user, String.format("title_%s", randomUUID()), String.format("content_%s", randomUUID()),
                category);
        Post savePost = postRepository.save(post);

        // When
        Post findPost = postRepository.findById(savePost.getId()).get();

        // Then
        Assertions.assertThat(findPost.getId()).isEqualTo(savePost.getId());
    }
    

    public String randomUUID() {
        return UUID.randomUUID().toString().substring(1, 10);
    }
}