package com.server.nodak.domain.post.repository;

import com.server.nodak.domain.post.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
}
