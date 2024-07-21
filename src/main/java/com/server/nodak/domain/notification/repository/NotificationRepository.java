package com.server.nodak.domain.notification.repository;

import com.server.nodak.domain.notification.dto.NotificationInfo;
import com.server.nodak.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT new com.server.nodak.domain.notification.dto.NotificationInfo(n.type, f, w, n.postId) " +
            "FROM Notification n " +
            "LEFT JOIN n.follower f " +
            "LEFT JOIN n.writer w " +
            "WHERE n.userId = :userId")
    List<NotificationInfo> findAllByUserId(@Param("userId") long userId);
}
