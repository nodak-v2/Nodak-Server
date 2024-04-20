package com.server.nodak.exception.common;

public class InternalServerErrorException extends BaseException {

    public InternalServerErrorException() {
        super(500, "서버에 이상이 발생하여 요청이 실패했습니다.");
    }

    public InternalServerErrorException(String message) {
        super(500, message);
    }
}
