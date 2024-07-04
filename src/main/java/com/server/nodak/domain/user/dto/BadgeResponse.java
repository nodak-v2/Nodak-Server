package com.server.nodak.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BadgeResponse {

    private Long posting;

    private Long follow;

    private Long voting;

    private Long comment;

    private Long like;

}
