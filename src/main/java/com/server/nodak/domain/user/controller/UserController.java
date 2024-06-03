package com.server.nodak.domain.user.controller;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.user.dto.CurrentUserInfoResponse;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.domain.user.dto.UserUpdateDTO;
import com.server.nodak.domain.user.service.UserService;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.aop.AuthorizationRequired;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.server.nodak.global.common.response.ApiResponse.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/status")
    @AuthorizationRequired({UserRole.GENERAL, UserRole.MANAGER})
    public ResponseEntity<ApiResponse<CurrentUserInfoResponse>> getStatus() {
        return ok(success(userService.getCurrentUserInfo()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@PathVariable("userId") Long userId) {
        return ok(success(userService.getUserInfo(userId)));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> getUserInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUserInfo(userUpdateDTO);
        return ok(success());
    }
}
