package com.server.nodak.domain.chat.service;

import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.randomUUID;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.server.nodak.domain.chat.domain.ChatRoom;
import com.server.nodak.domain.chat.domain.Message;
import com.server.nodak.domain.chat.dto.request.ChatRoomCreateRequest;
import com.server.nodak.domain.chat.dto.request.MessageRequest;
import com.server.nodak.domain.chat.dto.response.ChatRoomListResponse;
import com.server.nodak.domain.chat.repository.ChatRoomRepository;
import com.server.nodak.domain.chat.repository.MessageRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.domain.vote.utils.Utils;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    @Mock
    MessageRepository messageRepository;

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

    @Test
    @DisplayName("채팅방 조회 테스트")
    public void findChatRoom() {
        // given
        long requesterId = randomLongValue();
        PageRequest pageRequest = PageRequest.of(0, 10);
        ChatRoomListResponse chatRoomListResponse = new ChatRoomListResponse(randomLongValue(), randomUUID());
        Page<ChatRoomListResponse> expectResponse = new PageImpl<>(List.of(chatRoomListResponse), pageRequest, 0);
        given(chatRoomRepository.findChatRoom(requesterId, pageRequest)).willReturn(expectResponse);

        // when
        Page<ChatRoomListResponse> chatRoomList = chatService.findChatRoomList(requesterId, pageRequest);

        // then
        Assertions.assertThat(chatRoomList).isEqualTo(expectResponse);
    }

    @Test
    @DisplayName("채팅 메세지 저장 테스트")
    public void saveMessage() {
        // given
        long requesterId = randomLongValue();
        long chatRoomId = randomLongValue();
        User requester = Utils.createUser();
        User acceptor = Utils.createUser();
        ChatRoom chatRoom = createChatRoom(requester, acceptor);
        MessageRequest request = MessageRequest.builder()
                .content(randomUUID())
                .build();
        Message message = Message.builder()
                .sendUser(requester)
                .chatRoom(chatRoom)
                .content(request.getContent())
                .build();

        given(userRepository.findById(requesterId)).willReturn(Optional.of(requester));
        given(chatRoomRepository.findById(chatRoomId)).willReturn(Optional.of(chatRoom));

        // when
        chatService.saveMessage(requesterId, chatRoomId, request);

        // then
        then(messageRepository).should().save(message);
    }

    private long randomLongValue() {
        return rnd.nextLong(1, 10);
    }

    private ChatRoom createChatRoom(User requester, User acceptor) {
        return ChatRoom.builder()
                .acceptor(acceptor)
                .requester(requester)
                .build();
    }

}