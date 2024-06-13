package com.server.nodak.domain.user.service;

import com.server.nodak.domain.follow.repository.FollowRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserHistory;
import com.server.nodak.domain.user.dto.CurrentUserInfoResponse;
import com.server.nodak.domain.user.dto.UserHistoryListResponse;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.domain.user.dto.UserUpdateDTO;
import com.server.nodak.domain.user.repository.UserHistoryRepository;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.DataNotFoundException;
import com.server.nodak.security.SecurityUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {
    private final static int HISTORY_EXPIRATION_DAYS = 30;
    private final UserRepository userRepository;
    private final UserHistoryRepository userHistoryRepository;
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
        List<UserHistoryListResponse> userHistory = getUserHistory(userId);

        return UserInfoResponse.of(user, followerCount, followeeCount, userHistory);
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

    @Transactional(readOnly = true)
    public List<UserHistoryListResponse> getUserHistory(long userId) {
        LocalDateTime endAt = LocalDateTime.now();
        LocalDateTime startAt = endAt.minusDays(HISTORY_EXPIRATION_DAYS - 1);

        List<UserHistory> histories = userHistoryRepository.findByActionDateTimeBetweenAndUserIdOrderByActionDateTime(
                startAt,
                endAt, userId);

        List<UserHistoryListResponse> result = new ArrayList<>();

        int historiesIdx = 0;

        if (histories.size() != 0) {
            for (long i = 0; i < HISTORY_EXPIRATION_DAYS; i++) {
                if (startAt.plusDays(i).toLocalDate()
                        .equals(histories.get(historiesIdx).getActionDateTime().toLocalDate())) {
                    result.add(histories.get(historiesIdx).toListResponse());
                    historiesIdx++;
                } else {
                    result.add(UserHistoryListResponse.builder()
                            .date(startAt.plusDays(i).toLocalDate())
                            .level(1L)
                            .build());
                }
            }
        } else {
            for (long i = 0; i < HISTORY_EXPIRATION_DAYS; i++) {
                result.add(UserHistoryListResponse.builder()
                        .date(startAt.plusDays(i).toLocalDate())
                        .level(1L)
                        .build());
            }
        }

        return result;
    }
}
