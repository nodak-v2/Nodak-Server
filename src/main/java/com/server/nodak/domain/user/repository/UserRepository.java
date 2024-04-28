package com.server.nodak.domain.user.repository;

import com.server.nodak.domain.user.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long userId);

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);
}
