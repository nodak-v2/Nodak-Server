package com.server.nodak.domain.chat.repository;

import com.server.nodak.domain.chat.domain.ChatRoom;
import com.server.nodak.domain.chat.dto.response.ChatRoomListResponse;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.utils.Utils;
import com.server.nodak.global.config.QueryDslConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class})
@DisplayName("ChatRoomRepository 테스트")
@Slf4j
class ChatRoomRepositoryTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    Random rnd = new Random();

    PageRequest pageRequest = PageRequest.of(0, 10);

    @Test
    @DisplayName("저장 테스트")
    public void save() {
        // given
        User requester = Utils.createUser();
        User acceptor = Utils.createUser();
        ChatRoom chatRoom = createChatRoom(requester, acceptor);

        // when
        ChatRoom saveChatRoom = chatRoomRepository.save(chatRoom);

        // then
        Assertions.assertThat(chatRoom.getId()).isEqualTo(saveChatRoom.getId());
        Assertions.assertThat(chatRoom.getRequester().getId()).isEqualTo(requester.getId());
        Assertions.assertThat(chatRoom.getAcceptor().getId()).isEqualTo(acceptor.getId());
    }

    @Test
    @DisplayName("채팅방 조회 테스트")
    public void findChatRoom() {
        // given
        List<ChatRoom> chatRooms = saveChatRoom(10);
        Long requestId = rnd.nextLong(1, chatRooms.size());
        List<Long> filterChatRoomIds = chatRooms.stream()
                .filter(chatRoom -> chatRoom.getRequester().getId() == requestId
                        || chatRoom.getAcceptor().getId() == requestId).map(chatRoom -> chatRoom.getId()).toList();

        // when
        Page<ChatRoomListResponse> fetch = chatRoomRepository.findChatRoom(requestId,
                pageRequest);

        // then
        fetch.stream().forEach(
                chatRoom -> Assertions.assertThat(filterChatRoomIds.contains(chatRoom.getChatRoomId())).isTrue());
        fetch.forEach(e -> System.out.println(e.getChatRoomName()));
    }

    @Test
    @DisplayName("채팅방 삭제 테스트")
    public void deleteById() {
        // given
        User requester = Utils.createUser();
        User acceptor = Utils.createUser();
        ChatRoom chatRoom = createChatRoom(requester, acceptor);
        chatRoomRepository.save(chatRoom);

        // when
        chatRoomRepository.deleteById(chatRoom.getId());

        // then
        Assertions.assertThat(chatRoomRepository.existsById(chatRoom.getId())).isFalse();
    }

    private List<ChatRoom> saveChatRoom(int index) {
        List<ChatRoom> chatRooms = new ArrayList<>();
        IntStream.rangeClosed(1, index).forEach(i -> {
            User requester = Utils.createUser();
            User acceptor = Utils.createUser();
            ChatRoom chatRoom = createChatRoom(requester, acceptor);
            chatRooms.add(chatRoom);
            ChatRoom reverseChatRoom = createChatRoom(acceptor, requester);
            chatRooms.add(reverseChatRoom);
        });

        chatRoomRepository.saveAll(chatRooms);
        return chatRooms;
    }

    private ChatRoom createChatRoom(User requester, User acceptor) {
        return ChatRoom.builder()
                .requester(requester)
                .acceptor(acceptor)
                .build();
    }

}