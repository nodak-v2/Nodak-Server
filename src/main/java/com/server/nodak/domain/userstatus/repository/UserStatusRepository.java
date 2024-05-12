package com.server.nodak.domain.userstatus.repository;

import com.server.nodak.domain.userstatus.domain.UserStatus;

import java.util.List;
import java.util.Optional;

public interface UserStatusRepository {

    UserStatus put(UserStatus userStatus);

    Optional<UserStatus> findByUserId(Long userId);

    List<UserStatus> getAll();
}
