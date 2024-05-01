package com.server.nodak.exception.common;

public class UserNotAllowedException extends BaseException {
    public UserNotAllowedException() {
        super(403, "권한이 없습니다.");
    }

    public UserNotAllowedException(String message) {
        super(403, message);
    }
}
