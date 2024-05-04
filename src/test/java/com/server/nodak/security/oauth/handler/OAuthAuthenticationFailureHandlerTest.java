package com.server.nodak.security.oauth.handler;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;

@SpringBootTest
class OAuthAuthenticationFailureHandlerTest {
    @Autowired
    OAuthAuthenticationFailureHandler oAuthFailureHandler;

    @Test
    @DisplayName("OAuth 인증 실패시, RedirectUri이 설정되어 있으면 해당 Uri로 리다이렉트 되어야 한다.")
    void authenticationFailureWithRedirectCookie() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException exception = mock(AuthenticationException.class);

        String redirectUri = randomString();
        Cookie cookie = createCookie("redirect_uri", redirectUri);
        request.setCookies(cookie);

        // when
        oAuthFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        assertEquals(redirectUri, response.getHeader(LOCATION));
    }

    @Test
    @DisplayName("OAuth 인증 실패시, RedirectUri이 설정되어 있지 않으면 홈으로 리다이렉트 되어야 한다.")
    void authenticationFailureWithoutRedirectCookie() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException exception = mock(AuthenticationException.class);

        // when
        oAuthFailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        assertEquals("/", response.getHeader(LOCATION));
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private Cookie createCookie(String name, String value) {
        return new Cookie(name, value);
    }
}