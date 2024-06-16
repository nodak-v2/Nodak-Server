package com.server.nodak.domain.notification.event;

import com.server.nodak.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowEvent {

    private Long followerId;
    private String followerName;
    private Long followeeId;
    private String followeeName;
    private Long timestamp;

    public FollowEvent(User follower, User followee) {
        this.followerId = follower.getId();
        this.followerName = follower.getNickname();
        this.followeeId = followee.getId();
        this.followeeName = followee.getNickname();
        this.timestamp =  System.currentTimeMillis();
    }
}
