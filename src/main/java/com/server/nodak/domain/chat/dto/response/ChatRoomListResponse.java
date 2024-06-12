package com.server.nodak.domain.chat.dto.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomListResponse {

    private Long chatRoomId;

    private String chatRoomName;

    @QueryProjection
    public ChatRoomListResponse(Long chatRoomId, String chatRoomName) {
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
    }
}
