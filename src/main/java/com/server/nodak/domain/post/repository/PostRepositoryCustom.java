package com.server.nodak.domain.post.repository;

import com.server.nodak.domain.post.dto.PostSearchRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostSearchResponse> search(PostSearchRequest request, Pageable pageable);
}
