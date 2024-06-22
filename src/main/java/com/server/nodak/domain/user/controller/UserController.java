package com.server.nodak.domain.user.controller;

import static com.server.nodak.global.common.response.ApiResponse.success;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.ok;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.user.dto.CurrentUserInfoResponse;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.domain.user.dto.UserUpdateDTO;
import com.server.nodak.domain.user.service.UserService;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.SecurityUtils;
import com.server.nodak.security.aop.AuthorizationRequired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/status")
    @AuthorizationRequired(value = {UserRole.GENERAL, UserRole.MANAGER}, failureMessage = "로그인 정보가 없습니다.", status = OK)
    public ResponseEntity<ApiResponse<CurrentUserInfoResponse>> getStatus() {
        return ok(success("로그인 정보가 있습니다.", userService.getCurrentUserInfo()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@PathVariable("userId") Long userId,
                                                                     Principal principal) {
        UserInfoResponse userInfo;
        if (!SecurityUtils.isAuthenticated()) {
            userInfo = userService.getUserInfo(userId, null);
            return ok(success(userInfo));
        }
        userInfo = userService.getUserInfo(userId, Long.parseLong(principal.getName()));
        return ok(success(userInfo));
    }

    @PatchMapping
    @AuthorizationRequired({UserRole.GENERAL, UserRole.MANAGER})
    public ResponseEntity<ApiResponse<Void>> getUserInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUserInfo(userUpdateDTO);
        return ok(success());
    }

    @PostMapping
    @AuthorizationRequired({UserRole.GENERAL, UserRole.MANAGER})
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return ok(success());
    }

//    @GetMapping("/history")
//    @AuthorizationRequired({UserRole.GENERAL, UserRole.MANAGER})
//    public ResponseEntity<ApiResponse<List<UserHistoryListResponse>>> getUserHistory(Principal principal) {
//        long userId = Long.parseLong(principal.getName());
//
//        return ResponseEntity.ok(success(userService.getUserHistory(userId)));
//    }

}
