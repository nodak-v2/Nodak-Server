package com.server.nodak.domain.chat.repository;

import com.server.nodak.domain.chat.domain.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySendUserId(Long id);
}
