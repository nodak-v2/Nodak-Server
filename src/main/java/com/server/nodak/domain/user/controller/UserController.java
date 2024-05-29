package com.server.nodak.domain.user.controller;

import com.server.nodak.domain.user.service.UserService;
import com.server.nodak.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> getStatus() {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo()));
    }

}
