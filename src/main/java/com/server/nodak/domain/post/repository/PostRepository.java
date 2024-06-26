package com.server.nodak.domain.post.repository;

import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.dto.PostResponse;
import com.server.nodak.domain.post.dto.PostSearchRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Override
    Page<PostSearchResponse> search(PostSearchRequest request, Pageable pageable);

    @Override
    Optional<PostResponse> findOne(Long userId, Long postId);

    @Override
    List<PostSearchResponse> findUserInfo(Long userId);

    Optional<Post> findByIdAndUserId(Long postId, Long userId);

    List<Post> findByUserId(Long userId);

    @Override
    Page<PostSearchResponse> findMyPosting(Long userId, Pageable pageable);

    @Override
    Page<PostSearchResponse> findMyVoteHistory(Long userId, Pageable pageable);

    @Override
    Page<PostSearchResponse> findMyComment(Long userId, Pageable pageable);

    @Override
    Page<PostSearchResponse> findMyLike(Long userId, Pageable pageable);
}
