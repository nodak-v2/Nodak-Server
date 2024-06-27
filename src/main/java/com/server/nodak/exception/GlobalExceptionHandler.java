package com.server.nodak.exception;

import com.server.nodak.domain.user.dto.CurrentUserInfoResponse;
import com.server.nodak.exception.common.AuthorizationException;
import com.server.nodak.exception.common.BaseException;
import com.server.nodak.global.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBaseException(BaseException exception) {
        return ResponseEntity
                .status(exception.getCode())
                .body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> handleAuthorizationException(AuthorizationException exception) {
        return ResponseEntity
                .status(exception.getCode())
                .body(ApiResponse.success(exception.getMessage(), CurrentUserInfoResponse.of(null)));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Combine error messages into a single string or construct a meaningful message
        String errorMessage = errors.entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ApiResponse<?>> handleConstraintValidation(ConstraintViolationException ex) {
        String property = ex.getConstraintViolations().stream().map(e -> String.valueOf(e.getPropertyPath()))
                .collect(Collectors.joining(", "));
        String message = String.format("%s 은(는) %s", property, "다시 확인해주세요");
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }
}
