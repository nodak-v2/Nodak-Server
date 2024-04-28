package com.server.nodak.security.jwt;

import com.server.nodak.security.SecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;

@SpringBootTest
class JwtFilterTest {
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    JwtFilter jwtFilter;
    @MockBean
    SecurityService securityService;

    String accessToken;
    String subject;
    Authentication authentication;

    @BeforeEach
    void beforeEach() {
        subject = randomString();
        accessToken = tokenProvider.createAccessToken(subject);
        authentication = mock(Authentication.class);
    }

    @Test
    @DisplayName("올바른 토큰으로 필터 통과 시, SecurityContext가 정상적으로 설정되어야 한다.")
    void valid_token_certification() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader(AUTHORIZATION, accessToken);

        // when
        when(securityService.getAuthentication(tokenProvider.getSubject(accessToken)))
                .thenReturn(authentication);

        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("올바르지 않은 토큰으로 필터 통과 시, SecurityContext가 설정되면 안된다.")
    void invalid_token_certification() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader(AUTHORIZATION, accessToken + "1");

        // when
        when(securityService.getAuthentication(tokenProvider.getSubject(accessToken)))
                .thenReturn(authentication);

        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(securityService, never())
                .getAuthentication(anyString());
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

}