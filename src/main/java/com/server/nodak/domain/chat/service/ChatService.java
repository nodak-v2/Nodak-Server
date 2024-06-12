package com.server.nodak.domain.chat.service;

import com.server.nodak.domain.chat.domain.ChatRoom;
import com.server.nodak.domain.chat.dto.request.ChatRoomCreateRequest;
import com.server.nodak.domain.chat.repository.ChatRoomRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.DataNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException());
    }
}
