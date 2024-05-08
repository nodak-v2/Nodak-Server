package com.server.nodak.domain.comment.controller;

import com.server.nodak.domain.comment.dto.request.CreateCommentRequest;
import com.server.nodak.domain.comment.dto.request.UpdateCommentRequest;
import com.server.nodak.domain.comment.dto.response.CommentResponse;
import com.server.nodak.domain.comment.service.CommentService;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.aop.AuthorizationRequired;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable("postId") long postId,
            Principal principal,
            @Valid @RequestBody CreateCommentRequest commentRequest
    ) {
        commentService.createComment(postId, Long.parseLong(principal.getName()), commentRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping
    public ResponseEntity<?> getComments(@PathVariable("postId") long postId) {
        List<CommentResponse> result = commentService.fetchCommentsForPost(postId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{commentId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable("postId") long postId, @PathVariable("commentId") long commentId,
            @Valid @RequestBody UpdateCommentRequest commentRequest, Principal principal
    ) {
        commentService.updateComment(postId, Long.parseLong(principal.getName()), commentId, commentRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/{commentId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> deleteComment(
        @PathVariable("postId") long postId,
        Principal principal,
        @PathVariable("commentId") long commentId
    ) {
        commentService.deleteComment(postId, Long.parseLong(principal.getName()), commentId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
