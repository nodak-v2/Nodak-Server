package com.server.nodak.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.empty;

@Component
public class HttpServletUtils {

    public Optional<String> getHeader(HttpServletRequest request, String name) {
        return Optional.ofNullable(request.getHeader(name));
    }

    public void putHeader(HttpServletResponse response, String name, String value) {
        response.addHeader(name, value);
    }

    public void addCookie(HttpServletResponse response, String name, String value, int seconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(seconds);
        response.addCookie(cookie);
    }

    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        getCookie(request, name).ifPresent(cookie -> {
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        });
    }

    private Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if(cookies != null && cookies.length > 0) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findAny();
        }

        return empty();
    }
}
