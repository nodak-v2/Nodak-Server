package com.server.nodak.domain.post.repository;

import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.dto.PostSearchRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    @Override
    Page<PostSearchResponse> search(PostSearchRequest request, Pageable pageable);
}
