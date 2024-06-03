package com.server.nodak.domain.follow.domain;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Follow extends BaseEntity {
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY, optional = false)
    private User follower;
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY, optional = false)
    private User followee;
    private Boolean isDeleted;

    public static Follow create(User follower, User followee) {
        Follow follow = new Follow();
        follow.follower = follower;
        follow.followee = followee;
        follow.isDeleted = true;

        return follow;
    }

    public void updateDelete(boolean isDelete) {
        this.isDeleted = isDelete;
    }
}
