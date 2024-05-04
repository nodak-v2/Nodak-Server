package com.server.nodak.security;

import com.server.nodak.NodakApplication;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserProvider;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.InternalServerErrorException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

import static com.server.nodak.domain.user.domain.User.*;
import static com.server.nodak.domain.user.domain.UserProvider.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = NodakApplication.class)
@Transactional
class SecurityServiceImplTest {
    @Autowired
    SecurityService securityService;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    EntityManager em;

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("존재하는 유저의 id로 토큰 요청시 올바른 권한과 유저를 가진 토큰을 반환해야 한다.")
    void existsUserAuthentication(UserRole role) {
        // given
        User user = createUser(randomString(), randomString(), randomString(), KAKAO);
        user.updateRole(role);
        userRepository.save(user);
        flushAndClearPersistence();

        String userId = String.valueOf(user.getId());

        // when
        Authentication authentication = securityService.getAuthentication(userId);

        // then
        assertEquals(authentication.getPrincipal(), user);
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority(role.name())));
    }

    @Test
    @DisplayName("존재하지 않는 유저의 id로 인가 토큰 요청시, 예외가 발생해야한다.")
    void notExistsUserAuthentication() {
        // given
        String userId = String.valueOf(new Random().nextLong(100, 10000));

        // when

        // then
        assertThatThrownBy(() -> securityService.getAuthentication(userId))
                .isInstanceOf(InternalServerErrorException.class);
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}