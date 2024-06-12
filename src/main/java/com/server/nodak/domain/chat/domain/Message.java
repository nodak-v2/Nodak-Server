package com.server.nodak.domain.chat.domain;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    User sendUser;
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ChatRoom chatRoom;
    private String content;

    @Builder
    public Message(String content, User sendUser, ChatRoom chatRoom) {
        this.content = content;
        this.sendUser = sendUser;
        this.chatRoom = chatRoom;
    }

}
