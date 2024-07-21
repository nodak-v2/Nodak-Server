package com.server.nodak.domain.notification.entity;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.notification.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseEntity {

    private NotificationType type;
    private Long followerId;
    private Long followeeId;
    private Long writerId;
    private Long postId;
    private Long timestamp;

    @Builder
    public Notification(NotificationType type, Long followerId, Long followeeId, Long writerId, Long postId, Long timestamp) {
        this.type = type;
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.writerId = writerId;
        this.postId = postId;
        this.timestamp = System.currentTimeMillis();
    }
}
