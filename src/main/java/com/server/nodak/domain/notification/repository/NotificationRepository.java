package com.server.nodak.domain.notification.repository;

import com.server.nodak.domain.notification.dto.NotificationInfo;
import com.server.nodak.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT new com.server.nodak.domain.notification.dto.NotificationInfo(n.type, n.follower, n.writer, n.postId) FROM Notification n WHERE n.userId = :userId")
    List<NotificationInfo> findAllByUserId(long userId);
}
