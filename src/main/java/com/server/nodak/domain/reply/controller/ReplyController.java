package com.server.nodak.domain.reply.controller;

import com.server.nodak.domain.reply.dto.CreateReplyRequest;
import com.server.nodak.domain.reply.dto.DeleteReplyRequest;
import com.server.nodak.domain.reply.dto.ReplyResponse;
import com.server.nodak.domain.reply.service.ReplyService;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.aop.AuthorizationRequired;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<ReplyResponse>> createReply(Principal principal,
                                                                  @RequestBody CreateReplyRequest createReplyRequest) {
        ReplyResponse result = replyService.createReply(Long.parseLong(principal.getName()),createReplyRequest);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> deleteReply(Principal principal,
                                                                  @RequestBody DeleteReplyRequest deleteRequest) {
        replyService.deleteReply(Long.parseLong(principal.getName()), deleteRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<List<ReplyResponse>>> getAllReply(@PathVariable("commentId") Long commentId) {
        List<ReplyResponse> result = replyService.getAllByCommentId(commentId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
