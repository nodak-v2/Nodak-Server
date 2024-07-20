package com.server.nodak.domain.vote.controller;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.vote.dto.VoteResponse;
import com.server.nodak.domain.vote.service.VoteService;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.SecurityUtils;
import com.server.nodak.security.aop.AuthorizationRequired;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<VoteResponse>> voteResult(Principal principal,
                                                                @PathVariable("postId") Long postId) {
        VoteResponse result;
        if (SecurityUtils.isAuthenticated()) {
            result = voteService.findVoteResult(Long.parseLong(principal.getName()), postId);
            return ResponseEntity.ok(ApiResponse.success(result));
        }
        result = voteService.findVoteResult(null, postId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @AuthorizationRequired(UserRole.GENERAL)
    @PostMapping("/{voteId}")
    public ResponseEntity<ApiResponse<Void>> vote(
            Principal principal,
            @PathVariable("voteId") Long voteId,
            @RequestParam Long option
    ) {
        voteService.registerVoteOption(Long.parseLong(principal.getName()), voteId, option);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
