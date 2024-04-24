package com.server.nodak.domain.user.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static com.server.nodak.domain.user.domain.User.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("User 엔티티 테스트")
@Slf4j
class UserTest {

    @Autowired
    TestEntityManager em;

    @ParameterizedTest
    @EnumSource(UserProvider.class)
    @DisplayName("User 엔티티 생성 테스트")
    void successCreateUser(UserProvider provider) throws Exception {
        //given
        User user = createUserEntity(provider);
        em.persist(user);

        flushAndClearPersistence();

        //when

        //then
        assertThat(user).isEqualTo(em.find(User.class, user.getId()));
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private User createUserEntity(UserProvider provider) {
        return createUser(
                randomString(),
                randomString(),
                randomString(),
                provider
        );
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}