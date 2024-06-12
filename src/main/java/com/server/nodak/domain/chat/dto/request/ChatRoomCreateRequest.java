package com.server.nodak.domain.chat.dto.request;

import com.server.nodak.domain.chat.domain.ChatRoom;
import com.server.nodak.domain.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRoomCreateRequest {

    @NotBlank
    private long acceptorId;

    public ChatRoom toEntity(User requester, User acceptor) {
        return ChatRoom.builder()
                .requester(requester)
                .acceptor(acceptor)
                .build();
    }
}
