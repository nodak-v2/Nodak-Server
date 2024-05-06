package com.server.nodak.domain.vote.controller;

import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.vote.dto.VoteResponse;
import com.server.nodak.domain.vote.service.VoteService;
import com.server.nodak.global.common.response.ApiResponse;
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

    // TODO : 로그인 회원 식별 어노테이션 추가 예정
    @GetMapping("/{voteId}")
    public ResponseEntity<ApiResponse<VoteResponse>> voteResult(@PathVariable Long voteId,
                                                                Principal principal) {

        VoteResponse result = voteService.findVoteResult(Long.parseLong(principal.getName()), voteId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{voteId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> registerVote(@PathVariable Long voteId, @RequestParam Long option,
                                                          Principal principal) {
        voteService.registerVoteOption(principal.getName(), voteId, option);
        return ResponseEntity.ok().build();
    }
}
