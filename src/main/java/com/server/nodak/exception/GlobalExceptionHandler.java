package com.server.nodak.exception;

import com.server.nodak.exception.common.BaseException;
import com.server.nodak.global.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBaseException(BaseException exception) {
        return ResponseEntity
                .status(exception.getCode())
                .body(ApiResponse.error(exception.getMessage()));
    }
}

