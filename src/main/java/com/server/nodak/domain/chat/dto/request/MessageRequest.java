package com.server.nodak.domain.chat.dto.request;

import com.server.nodak.domain.chat.dto.response.MessageResponse;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class MessageRequest {

    private String content;

    public MessageRequest(String content) {
        this.content = content;
    }

    public MessageResponse convertResponse() {
        return MessageResponse.builder()
                .content(this.getContent())
                .build();
    }
}
