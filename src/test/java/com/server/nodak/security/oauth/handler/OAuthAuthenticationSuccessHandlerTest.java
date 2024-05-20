package com.server.nodak.security.oauth.handler;

import com.server.nodak.NodakApplication;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.security.NodakAuthentication;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.assertj.core.api.Assertions;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;

@SpringBootTest
class OAuthAuthenticationSuccessHandlerTest {
    @Autowired
    OAuthAuthenticationSuccessHandler oAuthSuccessHandler;

//    @Test
//    @DisplayName("Redirect Uri 쿠키를 갖고 있는 요청이면 해당 Uri가 반환되어야 한다.")
//    void redirectWithCookie() {
//        // given
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        Authentication authentication = mock(Authentication.class);
//        String redirectValue = randomString();
//        Cookie cookie = createCookie("redirect_uri", redirectValue);
//
//        // when
//        when(request.getCookies())
//                .thenReturn(new Cookie[]{cookie});
//
//        // then
//        assertEquals(redirectValue, oAuthSuccessHandler.determineTargetUrl(request, response, authentication));
//    }

    @Test
    @DisplayName("인증 성공 시, 토큰이 반환되어야 한다.")
    void onSuccessThenCreateToken() throws Exception {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        NodakServletResponse response = new NodakServletResponse();
        NodakAuthentication authentication = mock(NodakAuthentication.class);
        OAuth2AuthenticationToken oAuth2AuthenticationToken = mock(OAuth2AuthenticationToken.class);
        User user = mock(User.class);

        // when
        when(user.getId()).thenReturn(1L);
        when(authentication.getUser()).thenReturn(user);
        when(oAuth2AuthenticationToken.getPrincipal()).thenReturn(authentication);

        oAuthSuccessHandler.onAuthenticationSuccess(request, response, oAuth2AuthenticationToken);

        // then
        List<String> cookieNames = response.cookies.stream().map(Cookie::getName)
                .toList();

        assertThat(cookieNames).contains("AccessToken", "RefreshToken");
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private Cookie createCookie(String name, String value) {
        return new Cookie(name, value);
    }

    static class NodakServletResponse extends MockHttpServletResponse {
        private List<Cookie> cookies = new ArrayList<>();

        @Override
        public void addCookie(Cookie cookie) {
            super.addCookie(cookie);
            cookies.add(cookie);
        }
    }
}