package com.server.nodak.security;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static com.server.nodak.domain.user.domain.UserProvider.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityUtilsTest {

    User user;
    Authentication authentication;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        user = User.createUser(randomString(), randomString(), randomString(), KAKAO);
        userRepository.save(user);
        authentication = new NodakAuthentication(user);
    }

    @Test
    @DisplayName("로그인한 유저는 로그인 여부가 true로 반환되어야 한다.")
    void authenticatedUserLogin() {
        // given

        // when
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // then
        assertTrue(SecurityUtils.isAuthenticated());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저는 로그인 여부가 false로 반환되어야 한다.")
    void notAuthenticatedUserLogin() {
        // given

        // when
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication1 = context.getAuthentication();
        // then
        assertFalse(SecurityUtils.isAuthenticated());
    }

    @Test
    @DisplayName("로그인 한 유저는 id 값을 반환해야한다.")
    void authenticatedUserGetId() {
        // given

        // when
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // then
        assertEquals(user.getId(), SecurityUtils.getUserId());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저는 null 값을 반환해야한다.")
    void notAuthenticatedUserGetId() {
        // given

        // when

        // then
        assertNull(SecurityUtils.getUserId());
    }

    @Test
    @DisplayName("로그인 한 유저는 유저를 반환해야한다.")
    void authenticatedUserGetUser() {
        // given

        // when
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // then
        assertEquals(user, SecurityUtils.getUser());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저는 null 값을 반환해야한다.")
    void notAuthenticatedUserGetUser() {
        // given

        // when

        // then
        assertNull(SecurityUtils.getUser());
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}