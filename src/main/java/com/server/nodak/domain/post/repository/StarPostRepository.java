package com.server.nodak.domain.post.repository;

import com.server.nodak.domain.post.domain.StarPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StarPostRepository extends JpaRepository<StarPost, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    List<StarPost> findByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT s FROM StarPost s WHERE s.user.id = :userId AND s.post.id = :postId")
    Optional<StarPost> findByDeletedIsTrue(Long userId, Long postId);
}
