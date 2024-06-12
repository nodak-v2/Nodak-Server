package com.server.nodak.domain.chat.repository;

import static com.server.nodak.domain.vote.utils.Utils.randomUUID;

import com.server.nodak.domain.chat.domain.ChatRoom;
import com.server.nodak.domain.chat.domain.Message;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.utils.Utils;
import com.server.nodak.global.config.QueryDslConfig;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class})
@DisplayName("MessageRepository 테스트")
@Slf4j
class MessageRepositoryTest {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    Random rnd = new Random();

    @Test
    @DisplayName("저장 테스트")
    public void save() {
        // given
        User requester = Utils.createUser();
        User acceptor = Utils.createUser();
        ChatRoom chatRoom = saveChatRoom(requester, acceptor);

        Message message = Message.builder().sendUser(requester)
                .content(randomUUID())
                .chatRoom(chatRoom)
                .build();

        // when
        Message saveMessage = messageRepository.save(message);

        // then
        Assertions.assertThat(message.getId()).isEqualTo(saveMessage.getId());

    }

    @Test
    public void findBySendUserId() {
        // given
        User requester = Utils.createUser();
        User acceptor = Utils.createUser();
        ChatRoom chatRoom = saveChatRoom(requester, acceptor);

        Message message = Message.builder().sendUser(requester)
                .content(randomUUID())
                .chatRoom(chatRoom)
                .build();

        messageRepository.save(message);

        // when
        List<Message> findMessages = messageRepository.findBySendUserId(message.getSendUser().getId());

        // then
        Assertions.assertThat(findMessages.get(0).getId()).isEqualTo(message.getId());
        Assertions.assertThat(findMessages.get(0).getSendUser().getId()).isEqualTo(message.getSendUser().getId());
    }

    private ChatRoom saveChatRoom(User requester, User acceptor) {
        ChatRoom chatRoom = createChatRoom(requester, acceptor);
        return chatRoomRepository.save(chatRoom);
    }

    private ChatRoom createChatRoom(User requester, User acceptor) {
        return ChatRoom.builder()
                .requester(requester)
                .acceptor(acceptor)
                .build();
    }
}