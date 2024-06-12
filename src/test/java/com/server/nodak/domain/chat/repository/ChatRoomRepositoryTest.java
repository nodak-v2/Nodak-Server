package com.server.nodak.domain.chat.repository;

import com.server.nodak.domain.chat.domain.ChatRoom;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.utils.Utils;
import com.server.nodak.global.config.QueryDslConfig;
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
@DisplayName("ChatRoomRepository 테스트")
@Slf4j
class ChatRoomRepositoryTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;

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

    private ChatRoom createChatRoom(User requester, User acceptor) {
        return ChatRoom.builder()
                .requester(requester)
                .acceptor(acceptor)
                .build();
    }

}