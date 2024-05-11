package com.server.nodak.domain.vote.controller;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.vote.dto.VoteResponse;
import com.server.nodak.domain.vote.service.VoteService;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.aop.AuthorizationRequired;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    @AuthorizationRequired(UserRole.GENERAL)
    @GetMapping("/{voteId}")
    public ResponseEntity<ApiResponse<VoteResponse>> voteResult(Principal principal, @PathVariable("voteId") Long voteId) {
        VoteResponse result = voteService.findVoteResult(Long.parseLong(principal.getName()), voteId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @AuthorizationRequired(UserRole.GENERAL)
    @PostMapping("/{voteId}")
    public ResponseEntity<ApiResponse<Void>> registerVote(
            Principal principal,
            @PathVariable("voteId") Long voteId,
            @RequestParam Long option
    ) {
        voteService.registerVoteOption(Long.parseLong(principal.getName()), voteId, option);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
