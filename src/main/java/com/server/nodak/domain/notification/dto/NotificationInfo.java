package com.server.nodak.domain.notification.dto;

import com.server.nodak.domain.notification.NotificationType;
import com.server.nodak.domain.notification.entity.Notification;
import com.server.nodak.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class NotificationInfo {

    private NotificationType type;
    private Long followerId;
    private String followerName;
    private Long writerId;
    private String writerName;
    private Long postId;
    private Long timestamp;

    @Builder
    public NotificationInfo(NotificationType type, User follower, User writer, Long postId, Long timestamp) {
        this.type = type;
        this.followerId = (follower != null) ? follower.getId() : null;
        this.followerName = (follower != null) ? follower.getNickname() : null;
        this.writerId = (writer != null) ? writer.getId() : null;
        this.writerName = (writer != null) ? writer.getNickname() : null;
        this.postId = postId;
        this.timestamp = timestamp;
    }
}
