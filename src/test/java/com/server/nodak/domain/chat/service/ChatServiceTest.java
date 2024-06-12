package com.server.nodak.domain.chat.service;

import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.server.nodak.domain.chat.domain.ChatRoom;
import com.server.nodak.domain.chat.dto.request.ChatRoomCreateRequest;
import com.server.nodak.domain.chat.repository.ChatRoomRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import java.util.Optional;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatService 테스트")
@Slf4j
class ChatServiceTest {

    @InjectMocks
    ChatService chatService;

    @Mock
    UserRepository userRepository;

    @Mock
    ChatRoomRepository chatRoomRepository;

    Random rnd = new Random();

    @Test
    @DisplayName("채팅방 저장 테스트")
    public void saveChatRoom() {
        // given
        long requesterId = randomLongValue();
        ChatRoomCreateRequest request = new ChatRoomCreateRequest(randomLongValue());
        User requester = createUser();
        User acceptor = createUser();
        ChatRoom chatRoom = request.toEntity(requester, acceptor);

        given(userRepository.findById(requesterId)).willReturn(Optional.of(requester));
        given(userRepository.findById(request.getAcceptorId())).willReturn(Optional.of(acceptor));

        // when
        chatService.saveChatRoom(requesterId, request);

        // then
        then(chatRoomRepository).should().save(chatRoom);
    }

    private long randomLongValue() {
        return rnd.nextLong(1, 10);
    }

}