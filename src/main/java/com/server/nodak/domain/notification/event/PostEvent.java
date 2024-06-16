package com.server.nodak.domain.notification.event;

import com.server.nodak.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostEvent {

    private Long postId;
    private Long writerId;
    private String writerName;
    private Long timestamp;

    public PostEvent(Long postId, Long writerId, String writerName) {
        this.postId = postId;
        this.writerId = writerId;
        this.writerName = writerName;
        this.timestamp = System.currentTimeMillis();
    }

    public PostEvent(Long postId, User user) {
        this(postId, user.getId(), user.getNickname());
    }
}
