package com.server.nodak.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;

    public CreateCommentRequest(String content) {
        this.content = content;
    }
}
