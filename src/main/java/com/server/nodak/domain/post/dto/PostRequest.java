package com.server.nodak.domain.post.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostRequest {
    private String title;
    private String content;
    private String imageUrl;
    private String channel;
    private String voteTitle;
    private Map<Integer, String> voteOptionContent;
}
