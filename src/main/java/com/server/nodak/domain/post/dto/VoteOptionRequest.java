package com.server.nodak.domain.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteOptionRequest {

    private String option;

    private String imageUrl;

}
