package com.server.nodak.domain.user.controller;

import static com.server.nodak.global.common.response.ApiResponse.success;
import static org.springframework.http.ResponseEntity.ok;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.user.dto.CurrentUserInfoResponse;
import com.server.nodak.domain.user.dto.UserHistoryListResponse;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.domain.user.dto.UserUpdateDTO;
import com.server.nodak.domain.user.service.UserService;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.aop.AuthorizationRequired;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @AuthorizationRequired({UserRole.GENERAL, UserRole.MANAGER})
    public ResponseEntity<ApiResponse<Void>> getUserInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUserInfo(userUpdateDTO);
        return ok(success());
    }

    @GetMapping("/history")
    @AuthorizationRequired({UserRole.GENERAL, UserRole.MANAGER})
    public ResponseEntity<ApiResponse<List<UserHistoryListResponse>>> getUserHistory(Principal principal) {
        long userId = Long.parseLong(principal.getName());

        return ResponseEntity.ok(success(userService.getUserHistory(userId)));
    }

}
