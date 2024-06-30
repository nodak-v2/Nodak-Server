package com.server.nodak.domain.user.service;

import com.server.nodak.domain.post.dto.PostSearchResponse;
import com.server.nodak.domain.post.repository.PostRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserHistory;
import com.server.nodak.domain.user.dto.CurrentUserInfoResponse;
import com.server.nodak.domain.user.dto.UserHistoryListResponse;
import com.server.nodak.domain.user.dto.UserInfoDTO;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.domain.user.dto.UserUpdateDTO;
import com.server.nodak.domain.user.repository.UserHistoryRepository;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.DataNotFoundException;
import com.server.nodak.security.SecurityUtils;
import com.server.nodak.utils.HttpServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final PostRepository postRepository;
    private final HttpServletUtils servletUtils;

    @Transactional(readOnly = true)
    public CurrentUserInfoResponse getCurrentUserInfo() {
        return CurrentUserInfoResponse.of(SecurityUtils.getUser());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        servletUtils.removeCookie(request, response, "AccessToken");
        servletUtils.removeCookie(request, response, "RefreshToken");
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId, Long myId) {
        UserInfoDTO userInfoDTO = userRepository.getUserInfo(userId, myId)
            .orElseThrow(() -> new DataNotFoundException());

        List<PostSearchResponse> userInfo = postRepository.findUserInfo(userId);

        UserInfoResponse userInfoResponse = userInfoDTO.toUserInfoResponse(userInfo);

        return userInfoResponse;
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
        int historiesLength = histories.size();
        if (historiesLength != 0) {
            for (long i = 0; i < HISTORY_EXPIRATION_DAYS; i++) {
                if (historiesLength <= historiesIdx) {
                    break;
                }

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
