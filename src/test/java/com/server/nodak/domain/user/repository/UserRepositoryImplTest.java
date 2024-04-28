package com.server.nodak.domain.user.repository;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.server.nodak.domain.user.domain.UserProvider.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryImplTest {
    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    User user;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(createRandomUser());
        flushAndClearPersistence();
    }

    @Test
    @DisplayName("ID로 유저 조회시 정상적으로 반환되어야 한다.")
    void findUserById() {
        // given

        // when

        // then
        assertThat(userRepository.findById(user.getId())).isPresent();
    }

    @Test
    @DisplayName("이메일로 유저 조회시 정상적으로 반환되어야 한다.")
    void findUserByEmail() {
        // given

        // when

        // then
        assertThat(userRepository.findByEmail(user.getEmail())).isPresent();
    }

    @Test
    @DisplayName("닉네임으로 유저 조회시 정상적으로 반환되어야 한다.")
    void findUserByNickname() {
        // given

        // when

        // then
        assertThat(userRepository.findByNickname(user.getNickname())).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 이메일, 닉네임으로 조회시 empty 값이 반환되어야 한다.")
    void findNotExistsUserByEmailAndNickname() {
        // given

        // when

        // then
        assertThat(userRepository.findByNickname(randomString())).isEmpty();
        assertThat(userRepository.findByEmail(randomString())).isEmpty();
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private User createRandomUser() {
        return User.createUser(randomString(), randomString(), randomString(), KAKAO);
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}