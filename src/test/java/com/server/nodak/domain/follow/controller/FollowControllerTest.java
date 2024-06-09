package com.server.nodak.domain.follow.controller;

import com.server.nodak.domain.follow.service.FollowService;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.global.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FollowControllerTest {

    @Mock
    private FollowService followService;

    @InjectMocks
    private FollowController followController;

    @Test
    public void followUserTest() {
        Principal principal = () -> "1";

        ResponseEntity<ApiResponse<Void>> response = followController.followUser(principal, 2L);

        verify(followService, times(1)).followUser(1L, 2L);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void unfollowUserTest() {
        Principal principal = () -> "1";

        ResponseEntity<ApiResponse<Void>> response = followController.unfollowUser(principal, 2L);

        verify(followService, times(1)).unfollowUser(1L, 2L);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void getFollowersTest() {
        Principal principal = () -> "1";
        UserInfoResponse userInfoResponse = new UserInfoResponse(1L, "email", "nickname", "profileImageUrl", "introduction", null, null, 1, 1);
        when(followService.getFollowers(anyLong())).thenReturn(Arrays.asList(userInfoResponse));

        ResponseEntity<ApiResponse<List<UserInfoResponse>>> response = followController.getFollowers(principal);

        verify(followService, times(1)).getFollowers(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getBody().size());
    }

    @Test
    public void getFolloweesTest() {
        Principal principal = () -> "1";
        UserInfoResponse userInfoResponse = new UserInfoResponse(1L, "email", "nickname", "profileImageUrl", "introduction", null, null, 1, 1);
        when(followService.getFollowees(anyLong())).thenReturn(Arrays.asList(userInfoResponse));

        ResponseEntity<ApiResponse<List<UserInfoResponse>>> response = followController.getFollowees(principal);

        verify(followService, times(1)).getFollowees(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getBody().size());
    }
}