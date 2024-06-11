package com.server.nodak.domain.notification.entity;

import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {

    private Long writerId;
    private String nickname;
    private Long postId;

    public Notification(User writer, Post post) {
        this.writerId = writer.getId();
        this.nickname = writer.getNickname();
        this.postId = post.getId();
    }
}
