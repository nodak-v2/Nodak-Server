package com.server.nodak.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private String message = "";
    private T body = null;

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(message, null);
    }

    public static <T> ApiResponse<T> success(T body) {
        return new ApiResponse<>("", body);
    }

    public static <T> ApiResponse<T> success(String message, T body) {
        return new ApiResponse<>(message, body);
    }

    public static ApiResponse success() {
        return new ApiResponse<>("", null);
    }
}
