package com.server.nodak.domain.post.repository;

import com.server.nodak.domain.post.domain.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByTitle(String channel);
}
