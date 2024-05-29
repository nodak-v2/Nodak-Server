package com.server.nodak.domain.user.service;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.security.NodakAuthentication;
import com.server.nodak.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static com.server.nodak.domain.user.domain.UserProvider.KAKAO;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    User user;
    Authentication authentication;

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @BeforeEach
    void beforeEach() {
        user = User.createUser(randomString(), randomString(), randomString(), KAKAO);
        userRepository.save(user);
        authentication = new NodakAuthentication(user);
    }

    @Test
    @DisplayName("로그인한 유저는 올바른 유저 정보를 받환받아야 한다.")
    void loginUserGetUserInfo() {
        // given

        // when
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // then
        UserInfoResponse userInfoResponse = userService.getUserInfo();

        assertEquals(user.getNickname(), userInfoResponse.getNickname());
        assertEquals(user.getId(), userInfoResponse.getUserId());
        assertTrue(userInfoResponse.isLogin());
        assertEquals(user.getProfileImageUrl(), userInfoResponse.getProfileImage());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저는 로그인 정보만 받환받아야 한다.")
    void notLoginUserGetUserInfo() {
        // given

        // when
        SecurityContextHolder.getContext().setAuthentication(null);

        // then
        UserInfoResponse userInfoResponse = userService.getUserInfo();

        assertFalse(userInfoResponse.isLogin());
        assertNull(userInfoResponse.getNickname());
        assertNull(userInfoResponse.getUserId());
        assertNull(userInfoResponse.getProfileImage());
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}