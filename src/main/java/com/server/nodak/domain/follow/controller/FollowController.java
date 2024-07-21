package com.server.nodak.domain.follow.controller;

import com.server.nodak.domain.follow.service.FollowService;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.user.dto.UserInfoDTO;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.SecurityUtils;
import com.server.nodak.security.aop.AuthorizationRequired;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{followeeId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> followUser(Principal principal,
        @PathVariable Long followeeId) {
        followService.followUser(Long.parseLong(principal.getName()), followeeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfollow/{followeeId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> unfollowUser(Principal principal,
        @PathVariable Long followeeId) {
        followService.unfollowUser(Long.parseLong(principal.getName()), followeeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<UserInfoDTO>>> getUserFollowers(
        @PathVariable("userId") Long userId, Principal principal) {
        if (SecurityUtils.isAuthenticated()) {
            return ResponseEntity.ok(ApiResponse.success(
                followService.getFollowers(Long.parseLong(principal.getName()), userId)));
        }
        return ResponseEntity.ok(ApiResponse.success(followService.getFollowers(null, userId)));
    }

    @GetMapping("/followees/{userId}")
    public ResponseEntity<ApiResponse<List<UserInfoDTO>>> getUserFollowees(
        @PathVariable("userId") Long userId, Principal principal) {
        if (SecurityUtils.isAuthenticated()) {
            return ResponseEntity.ok(ApiResponse.success(
                followService.getFollowees(Long.parseLong(principal.getName()), userId)));
        }
        return ResponseEntity.ok(ApiResponse.success(
            followService.getFollowees(null, userId)));
    }
}
