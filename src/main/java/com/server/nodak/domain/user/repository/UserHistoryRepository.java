package com.server.nodak.domain.user.repository;

import com.server.nodak.domain.user.domain.UserHistory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    Optional<UserHistory> findByUserId(Long userId);

    List<UserHistory> findByActionDateTimeBetween(LocalDateTime startAt, LocalDateTime endAt);

    List<UserHistory> findByActionDateTimeBetweenAndUserIdOrderByActionDateTime(LocalDateTime now,
                                                                                LocalDateTime localDateTime,
                                                                                Long userId);

    Optional<UserHistory> findByUserIdAndActionDateTime(Long userId, LocalDateTime now);

    List<UserHistory> findByActionDateTimeBefore(LocalDateTime localDateTime);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from UserHistory where actionDateTime < :localDateTime")
    void deleteAllByActionDateTimeBefore(@Param("localDateTime") LocalDateTime localDateTime);
}
