package com.server.nodak.domain.userstatus.repository;

import com.server.nodak.domain.userstatus.domain.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserStatusRepository implements UserStatusRepository {
    private final Map<Long, UserStatus> inMemoryRepository = new ConcurrentHashMap<>();

    @Override
    public UserStatus put(UserStatus userStatus) {
        return inMemoryRepository.put(userStatus.getUserId(), userStatus);
    }

    @Override
    public Optional<UserStatus> findByUserId(Long userId) {
        return Optional.ofNullable(inMemoryRepository.get(userId));
    }

    @Override
    public List<UserStatus> getAll() {
        return inMemoryRepository.values().stream()
                .toList();
    }
}
