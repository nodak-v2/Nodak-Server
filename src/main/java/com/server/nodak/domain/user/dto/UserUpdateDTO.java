package com.server.nodak.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateDTO {

    private String nickname;

    private String profileImageUrl;

    private String description;
}
