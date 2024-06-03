package com.server.nodak.domain.user.service;

import com.server.nodak.domain.follow.repository.FollowRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.CurrentUserInfoResponse;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.domain.user.dto.UserUpdateDTO;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.DataNotFoundException;
import com.server.nodak.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public CurrentUserInfoResponse getCurrentUserInfo() {
        return CurrentUserInfoResponse.of(SecurityUtils.getUser());
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        User user = getUserById(userId);

        int followerCount = followRepository.getUserFollowerCount(userId);
        int followeeCount = followRepository.getUserFolloweeCount(userId);

        return UserInfoResponse.of(user, followerCount, followeeCount);
    }

    @Transactional
    public void updateUserInfo(UserUpdateDTO userUpdateDTO) {
        User currentUser = getUserById(SecurityUtils.getUserId());

        if (StringUtils.hasText(userUpdateDTO.getNickname())) {
            currentUser.updateNickname(userUpdateDTO.getNickname());
        }

        if (StringUtils.hasText(userUpdateDTO.getDescription())) {
            currentUser.updateDescription(userUpdateDTO.getDescription());
        }

        if (StringUtils.hasText(userUpdateDTO.getProfileImageUrl())) {
            currentUser.updateImage(userUpdateDTO.getProfileImageUrl());
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(DataNotFoundException::new);
    }
}
