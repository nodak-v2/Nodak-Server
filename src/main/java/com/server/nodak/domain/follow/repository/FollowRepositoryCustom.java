package com.server.nodak.domain.follow.repository;

import com.server.nodak.domain.user.dto.UserInfoDTO;
import java.util.List;

public interface FollowRepositoryCustom {
    List<UserInfoDTO> findFollowersByUserId(Long userId);

    List<UserInfoDTO> findFolloweesByUserId(Long userId);
}
