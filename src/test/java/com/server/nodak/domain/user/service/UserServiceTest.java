package com.server.nodak.domain.user.service;

import static com.server.nodak.domain.user.domain.UserProvider.KAKAO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.CurrentUserInfoResponse;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.security.NodakAuthentication;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

//    @Test
    public void logout() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        given(request.getCookies()).willReturn(
                new Cookie[]{new Cookie("AccessToken", randomString()), new Cookie("RefreshToken", randomString())});

        // when
        userService.logout(request, response);

        // then
        String accessToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("AccessToken")).findFirst().get().getValue();
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("RefreshToken")).findFirst().get().getValue();

        Assertions.assertThat(accessToken).isEqualTo("");
        Assertions.assertThat(refreshToken).isEqualTo("");
    }

    void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int seconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(seconds);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }

    @Test
    @DisplayName("로그인한 유저는 올바른 유저 정보를 받환받아야 한다.")
    void loginUserGetUserInfo() {
        // given

        // when
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // then
        CurrentUserInfoResponse currentUserInfoResponse = userService.getCurrentUserInfo();

        assertEquals(user.getNickname(), currentUserInfoResponse.getNickname());
        assertEquals(user.getId(), currentUserInfoResponse.getUserId());
        assertTrue(currentUserInfoResponse.isLogin());
        assertEquals(user.getProfileImageUrl(), currentUserInfoResponse.getProfileImage());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저는 로그인 정보만 받환받아야 한다.")
    void notLoginUserGetUserInfo() {
        // given

        // when
        SecurityContextHolder.getContext().setAuthentication(null);

        // then
        CurrentUserInfoResponse currentUserInfoResponse = userService.getCurrentUserInfo();

        assertFalse(currentUserInfoResponse.isLogin());
        assertNull(currentUserInfoResponse.getNickname());
        assertNull(currentUserInfoResponse.getUserId());
        assertNull(currentUserInfoResponse.getProfileImage());
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}