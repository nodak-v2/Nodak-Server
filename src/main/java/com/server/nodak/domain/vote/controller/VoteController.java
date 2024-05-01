package com.server.nodak.domain.vote.controller;

import com.server.nodak.domain.vote.dto.VoteResultResponse;
import com.server.nodak.domain.vote.service.VoteService;
import com.server.nodak.global.common.response.ApiResponse;
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

    @GetMapping("/{voteId}")
    public ResponseEntity<ApiResponse<VoteResultResponse>> voteResult(@PathVariable Long voteId) {
        VoteResultResponse result = voteService.findVoteResult(voteId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{voteId}")
    public ResponseEntity<ApiResponse<Void>> registerVote(@PathVariable Long voteId, @RequestParam Long option,
                                                          Principal principal) {
        voteService.registerVoteOption(principal.getName(), voteId, option);
        return ResponseEntity.ok().build();
    }
}
