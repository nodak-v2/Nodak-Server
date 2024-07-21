package com.server.nodak.domain.notification.dto;

import com.server.nodak.domain.notification.NotificationType;
import com.server.nodak.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    public NotificationInfo(NotificationType type, User follower, User writer, Long postId) {
        this.type = type;
        this.followerId = follower.getId();
        this.followerName = follower.getNickname();
        this.writerId = writer.getId();
        this.writerName = writer.getNickname();
        this.postId = postId;
        this.timestamp = System.currentTimeMillis();
    }
}
