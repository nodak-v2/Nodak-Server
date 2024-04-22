package com.server.nodak.domain.post.domain;

import com.server.nodak.domain.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static com.server.nodak.domain.user.domain.UserProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Post 엔티티 생성 테스트")
@Slf4j
class PostTest {

    @Autowired
    private TestEntityManager em;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.createUser("user1@email.com", "pw123", "닉네임1", KAKAO);
        em.persist(user);
        em.flush();
    }

    @Test
    @DisplayName("Post 엔티티 저장 테스트")
    void 게시글_저장_테스트() {
        // given
        Post post = Post.builder()
                .title("post1")
                .content("게시글 내용 AA. 투표 부탁드립니다.")
                .imageUrl("abc.abc")
                .user(user)
                .build();

        em.persist(post);

        assertThat(post).isEqualTo(em.find(Post.class, post.getId()));
    }
}