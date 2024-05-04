package com.server.nodak.utils;

import com.server.nodak.NodakApplication;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = NodakApplication.class)
class HttpServletUtilsTest {

    @Autowired
    HttpServletUtils httpServletUtils;

    @Test
    @DisplayName("request 헤더에 값이 없으면 empty값이 반환되어야 한다.")
    void requestHeaderNotPresent() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when

        // then
        assertEquals(Optional.empty(), httpServletUtils.getHeader(request, randomString()));
    }

    @Test
    @DisplayName("requset 헤더에 값이 있으면 올바르게 반환되어야 한다.")
    void requestHeaderPresent() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String name = randomString();
        String value = randomString();
        request.addHeader(name, value);

        // when

        // then
        assertEquals(value, httpServletUtils.getHeader(request, name).get());
    }

    @Test
    @DisplayName("putHeader 메소드는 헤더에 값을 정상적으로 삽입해야 한다.")
    void putRequestHeader() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();
        String name = randomString();
        String value = randomString();

        // when
        httpServletUtils.putHeader(response, name, value);

        // then
        assertEquals(value, response.getHeader(name));
    }

    @Test
    @DisplayName("addCookie 메소드는 쿠키 값을 정상적으로 삽입해야한다.")
    void responseAddCookie() {
        // given
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        String name = randomString();
        String value = randomString();
        int seconds = new Random().nextInt();

        // when
        httpServletUtils.addCookie(mockResponse, name, value, seconds);

        // then
        verify(mockResponse, times(1))
                .addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("쿠키 삭제시 정상적으로 값이 삭제되어야 한다.")
    void responseRemoveCookie() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Cookie cookie = new Cookie(randomString(), randomString());

        // when
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // then
        httpServletUtils.removeCookie(request, response, cookie.getName());

        assertTrue(0 >= cookie.getMaxAge());
        assertEquals("", cookie.getValue());
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}