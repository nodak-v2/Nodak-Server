package com.server.nodak.domain.post.controller;

import com.server.nodak.domain.post.dto.PostRequest;
import com.server.nodak.domain.post.dto.PostResponse;
import com.server.nodak.domain.post.dto.PostSearchRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import com.server.nodak.domain.post.service.PostService;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.SecurityUtils;
import com.server.nodak.security.aop.AuthorizationRequired;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostSearchResponse>>> postSearch(PostSearchRequest request,
                                                                            @PageableDefault Pageable pageable) {
        Page<PostSearchResponse> response = postService.findPostBySearch(request, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> postDetails(@PathVariable Long postId) {
        PostResponse response = postService.findPost(SecurityUtils.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> registerPost(@RequestBody PostRequest request, Principal principal) {
        postService.savePost(Long.parseLong(principal.getName()), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/stars")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> registerLike(@PathVariable Long postId, Principal principal) {
        postService.registerLike(Long.parseLong(principal.getName()), postId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{postId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> updatePost(@PathVariable Long postId,
                                                        @RequestBody PostRequest request, Principal principal) {
        postService.updatePost(Long.parseLong(principal.getName()), postId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/stars")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> cancleLike(@PathVariable Long postId, Principal principal) {
        postService.cancleLike(Long.parseLong(principal.getName()), postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId, Principal principal) {
        postService.removePost(Long.parseLong(principal.getName()), postId);
        return ResponseEntity.ok().build();
    }
}
