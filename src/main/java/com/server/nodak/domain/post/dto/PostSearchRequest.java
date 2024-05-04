package com.server.nodak.domain.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSearchRequest {
    private String keyword;
    private Long categoryId;
}
