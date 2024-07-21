package com.server.nodak.domain.notification.entity;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.notification.NotificationType;
import com.server.nodak.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private NotificationType type;
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "follower_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "writer_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User writer;

    private Long postId;
    private Long timestamp;

    @Builder
    public Notification(NotificationType type, Long userId, User follower, User writer, Long postId) {
        this.type = type;
        this.userId = userId;
        this.follower= follower;
        this.writer = writer;
        this.postId = postId;
        this.timestamp = System.currentTimeMillis();
    }
}
