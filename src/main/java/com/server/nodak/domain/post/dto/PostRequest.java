package com.server.nodak.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostRequest {

    private String content;
    private String imageUrl;
    private String channel;
    private String voteTitle;
    private List<VoteOptionRequest> voteOptionContent;
    private LocalDateTime endDate;
}
