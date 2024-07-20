package com.server.nodak.domain.notification.entity;

import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Notification implements Serializable {

    private Long postId;
    private String message;
    private Long timestamp;
    private Long writerId;

    public Notification(Long postId, String message, Long writerId) {
        this.postId = postId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.writerId = writerId;
    }
}
