package com.server.nodak.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenProviderTest {
    @Autowired
    TokenProvider tokenProvider;

    String subject;
    String accessToken;
    String refreshToken;
    Random r = new Random();

    @BeforeEach
    void beforeEach() {
        this.subject = getRandomString();
        this.accessToken = tokenProvider.createAccessToken(subject);
        this.refreshToken = tokenProvider.createRefreshToken(subject);
    }

    @Test
    @DisplayName("올바르지 않은 토큰 유효성 검사 시, false 반환")
    void invalidTokenValidationValidationTest() {
        // given
        String invalidAccessToken1 = r.nextInt(10) + accessToken;
        String invalidAccessToken2 = accessToken + r.nextInt(10);
        String invalidRefreshToken1 = r.nextInt(10) + refreshToken;
        String invalidRefreshToken2 = refreshToken + r.nextInt(10);

        // when

        // then
        assertFalse(tokenProvider.validateToken(""));
        assertFalse(tokenProvider.validateToken(getRandomString()));
        assertFalse(tokenProvider.validateToken(invalidAccessToken1));
        assertFalse(tokenProvider.validateToken(invalidAccessToken2));
        assertFalse(tokenProvider.validateToken(invalidRefreshToken1));
        assertFalse(tokenProvider.validateToken(invalidRefreshToken2));
    }

    @Test
    @DisplayName("올바른 토큰은 유효성 유효성 검사 시, true 반환")
    void validTokenValidationTest() {
        // given

        // when

        // then
        assertTrue(tokenProvider.validateToken(accessToken));
        assertTrue(tokenProvider.validateToken(refreshToken));
    }

    @Test
    @DisplayName("올바른 토큰은 올바른 subject를 반환해야 한다.")
    void validTokenSubject() {
        // given

        // when

        // then
        assertEquals(subject, tokenProvider.getSubject(accessToken));
        assertEquals(subject, tokenProvider.getSubject(refreshToken));
    }

    private String getRandomString() {
        return UUID.randomUUID().toString();
    }
}