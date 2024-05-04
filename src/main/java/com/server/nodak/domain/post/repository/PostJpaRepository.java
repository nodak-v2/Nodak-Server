package com.server.nodak.domain.post.repository;

import com.server.nodak.domain.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
}