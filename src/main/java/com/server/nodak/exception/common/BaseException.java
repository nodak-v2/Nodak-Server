package com.server.nodak.exception.common;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private int code;

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }
}
