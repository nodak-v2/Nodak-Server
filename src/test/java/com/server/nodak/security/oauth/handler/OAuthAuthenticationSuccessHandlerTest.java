package com.server.nodak.security.oauth.handler;

import com.server.nodak.NodakApplication;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.security.NodakAuthentication;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;

@SpringBootTest
@ContextConfiguration(classes = NodakApplication.class)
class OAuthAuthenticationSuccessHandlerTest {
    @Autowired
    OAuthAuthenticationSuccessHandler oAuthSuccessHandler;

    @Test
    @DisplayName("Redirect Uri 쿠키를 갖고 있는 요청이면 해당 Uri가 반환되어야 한다.")
    void redirectWithCookie() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);
        String redirectValue = randomString();
        Cookie cookie = createCookie("redirect_uri", redirectValue);

        // when
        when(request.getCookies())
                .thenReturn(new Cookie[]{cookie});

        // then
        assertEquals(redirectValue, oAuthSuccessHandler.determineTargetUrl(request, response, authentication));
    }

    @Test
    @DisplayName("인증 성공 시, 토큰이 반환되어야 한다.")
    void onSuccessThenCreateToken() throws Exception {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        NodakAuthentication authentication = mock(NodakAuthentication.class);
        OAuth2AuthenticationToken oAuth2AuthenticationToken = mock(OAuth2AuthenticationToken.class);
        User user = mock(User.class);

        // when
        when(user.getId()).thenReturn(1L);
        when(authentication.getUser()).thenReturn(user);
        when(oAuth2AuthenticationToken.getPrincipal()).thenReturn(authentication);

        oAuthSuccessHandler.onAuthenticationSuccess(request, response, oAuth2AuthenticationToken);

        // then
        assertNotNull(response.getHeader(AUTHORIZATION));
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private Cookie createCookie(String name, String value) {
        return new Cookie(name, value);
    }
}