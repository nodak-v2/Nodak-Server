package com.server.nodak.domain.post.repository;

import com.server.nodak.domain.post.dto.PostResponse;
import com.server.nodak.domain.post.dto.PostSearchRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    List<PostSearchResponse> findUserInfo(Long userId);

    Page<PostSearchResponse> search(PostSearchRequest request, Pageable pageable);

    Optional<PostResponse> findOne(Long userId, Long postId);

    Page<PostSearchResponse> findMyPosting(Long userId, Pageable pageable);

    Page<PostSearchResponse> findMyVoteHistory(Long userId, Pageable pageable);

    Page<PostSearchResponse> findMyComment(Long userId, Pageable pageable);

    Page<PostSearchResponse> findMyLike(Long userId, Pageable pageable);
}
