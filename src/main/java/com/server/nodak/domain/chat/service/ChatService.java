package com.server.nodak.domain.chat.service;

import com.server.nodak.domain.chat.domain.ChatRoom;
import com.server.nodak.domain.chat.dto.request.ChatRoomCreateRequest;
import com.server.nodak.domain.chat.dto.response.ChatRoomListResponse;
import com.server.nodak.domain.chat.repository.ChatRoomRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;

    private final ChatRoomRepository chatRoomRepository;


    @Transactional
    public void saveChatRoom(long requesterId, ChatRoomCreateRequest request) {

        User requester = findUserById(requesterId);

        User acceptor = findUserById(request.getAcceptorId());

        ChatRoom chatRoom = request.toEntity(requester, acceptor);

        chatRoomRepository.save(chatRoom);
    }

    @Transactional(readOnly = true)
    public Page<ChatRoomListResponse> findChatRoomList(long requesterId, Pageable pageable) {
        return chatRoomRepository.findChatRoom(requesterId, pageable);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException());
    }
}
