package com.server.nodak.domain.follow.service;

import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.follow.repository.FollowRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserProvider;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowService followService;

    User follower;
    User followee;

    @BeforeEach
    public void setUp() {
        follower = Mockito.spy(User.createUser("follower@example.com", "password", "follower", UserProvider.KAKAO));
        followee = Mockito.spy(User.createUser("followee@example.com", "password", "followee", UserProvider.KAKAO));

        given(follower.getId()).willReturn(1L);
        given(followee.getId()).willReturn(2L);
    }

    @Test
    @DisplayName("유저 팔로우")
    public void followUserTest() {
        given(userRepository.findById(1L)).willReturn(Optional.of(follower));
        given(userRepository.findById(2L)).willReturn(Optional.of(followee));
        given(followRepository.getFollowByRelation(anyLong(), anyLong())).willReturn(Optional.empty());

        followService.followUser(1L, 2L);

        verify(followRepository, times(1)).save(any(Follow.class));
    }

    @Test
    @DisplayName("유저 팔로우 이미 존재")
    public void followUserAlreadyFollowingTest() {
        given(userRepository.findById(1L)).willReturn(Optional.of(follower));
        given(userRepository.findById(2L)).willReturn(Optional.of(followee));
        given(followRepository.getFollowByRelation(anyLong(), anyLong())).willReturn(Optional.of(new Follow()));

        assertThrows(BadRequestException.class, () -> followService.followUser(1L, 2L));
    }

    @Test
    @DisplayName("유저 언팔로우")
    public void unfollowUserTest() {
        Follow follow = new Follow();

        given(userRepository.findById(1L)).willReturn(Optional.of(follower));
        given(userRepository.findById(2L)).willReturn(Optional.of(followee));
        given(followRepository.getFollowByRelation(anyLong(), anyLong())).willReturn(Optional.of(follow));

        followService.unfollowUser(1L, 2L);
        verify(followRepository, times(1)).save(follow);
    }

    @Test
    @DisplayName("언팔로우 실패 - 존재하지 않는 관계")
    public void unfollowUserNotFoundTest() {
        given(followRepository.getFollowByRelation(anyLong(), anyLong())).willReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> followService.unfollowUser(1L, 2L));
    }

    @Test
    @DisplayName("팔로워 리스트 가져오기")
    public void getFollowersTest() {
        List<User> followers = Arrays.asList(follower);
        given(followRepository.getFollowersByUserId(anyLong())).willReturn(followers);
        given(followRepository.getUserFollowerCount(anyLong())).willReturn(1);
        given(followRepository.getUserFolloweeCount(anyLong())).willReturn(1);

        List<UserInfoResponse> result = followService.getFollowers(2L);

        assertEquals(1, result.size());
        assertEquals(follower.getId(), result.get(0).getUserId());
    }

    @Test
    @DisplayName("팔로우 리스트 가져오기")
    public void getFolloweesTest() {
        List<User> followees = Arrays.asList(followee);
        given(followRepository.getFolloweesByUserId(anyLong())).willReturn(followees);
        given(followRepository.getUserFollowerCount(anyLong())).willReturn(1);
        given(followRepository.getUserFolloweeCount(anyLong())).willReturn(1);

        List<UserInfoResponse> result = followService.getFollowees(1L);

        assertEquals(1, result.size());
        assertEquals(followee.getId(), result.get(0).getUserId());
    }
}