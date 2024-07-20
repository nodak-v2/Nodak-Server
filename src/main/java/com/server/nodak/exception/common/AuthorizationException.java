package com.server.nodak.exception.common;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public class AuthorizationException extends BaseException {
    public AuthorizationException() {
        super(UNAUTHORIZED.value(), "접근 권한이 없습니다.");
    }

    public AuthorizationException(String message) {
        super(UNAUTHORIZED.value(), message);
    }

    public AuthorizationException(int code, String message) {
        super(code, message);
    }
}
