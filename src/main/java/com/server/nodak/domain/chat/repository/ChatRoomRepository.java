package com.server.nodak.domain.chat.repository;


import com.server.nodak.domain.chat.domain.ChatRoom;
import com.server.nodak.domain.chat.dto.response.ChatRoomListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomCustomRepository {
    @Override
    Page<ChatRoomListResponse> findChatRoom(long requesterId, Pageable pageable);
}
