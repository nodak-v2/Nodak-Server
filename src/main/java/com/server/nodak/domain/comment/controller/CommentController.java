package com.server.nodak.domain.comment.controller;

import com.server.nodak.domain.comment.dto.request.CreateCommentRequest;
import com.server.nodak.domain.comment.dto.response.CommentResponse;
import com.server.nodak.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(
            @PathVariable("postId") long postId,
            @RequestBody CreateCommentRequest commentRequest
    ) {
        commentService.createComment(postId, commentRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable("postId") long postId) {
        List<CommentResponse> result = commentService.fetchCommentsForPost(postId);
        return ResponseEntity.ok(result);
    }
}
