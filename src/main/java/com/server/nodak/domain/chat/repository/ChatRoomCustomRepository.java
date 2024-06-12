package com.server.nodak.domain.chat.repository;

import com.server.nodak.domain.chat.dto.response.ChatRoomListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomCustomRepository {
    Page<ChatRoomListResponse> findChatRoom(long requesterId, Pageable pageable);
}
