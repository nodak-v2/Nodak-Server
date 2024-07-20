package com.server.nodak.domain.chat.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
public class MessageResponse {

    private final String content;

    private final LocalDateTime sendDate;

    @Builder
    public MessageResponse(String content, LocalDateTime sendDate) {
        this.content = content;
        this.sendDate = sendDate;
    }
}
