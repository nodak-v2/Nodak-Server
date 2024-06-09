package com.server.nodak.domain.follow.controller;

import com.server.nodak.domain.follow.service.FollowService;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.aop.AuthorizationRequired;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{followeeId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> followUser(Principal principal, @PathVariable Long followeeId) {
        followService.followUser(Long.parseLong(principal.getName()), followeeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfollow/{followeeId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> unfollowUser(Principal principal, @PathVariable Long followeeId) {
        followService.unfollowUser(Long.parseLong(principal.getName()), followeeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<UserInfoResponse>>> getFollowers(Principal principal) {
        List<UserInfoResponse> followers = followService.getFollowers(Long.parseLong(principal.getName()));
        return ResponseEntity.ok(ApiResponse.success(followers));
    }

    @GetMapping("/followees")
    public ResponseEntity<ApiResponse<List<UserInfoResponse>>> getFollowees(Principal principal) {
        List<UserInfoResponse> followees = followService.getFollowees(Long.parseLong(principal.getName()));
        return ResponseEntity.ok(ApiResponse.success(followees));
    }
}
