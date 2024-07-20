package com.server.nodak.domain.follow.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.server.nodak.domain.follow.service.FollowService;
import com.server.nodak.domain.user.dto.UserInfoDTO;
import com.server.nodak.global.common.response.ApiResponse;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

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
        UserInfoDTO userInfoDTO = new UserInfoDTO(1L, "email", "nickname", "profileImageUrl", "introduction", null,
                null, 1L, 1L);
        when(followService.getFollowers(anyLong())).thenReturn(List.of(userInfoDTO));

        ResponseEntity<ApiResponse<List<UserInfoDTO>>> response = followController.getFollowers(principal);

        verify(followService, times(1)).getFollowers(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getBody().size());
    }

    @Test
    public void getFolloweesTest() {
        Principal principal = () -> "1";
        UserInfoDTO userInfoDTO = new UserInfoDTO(1L, "email", "nickname", "profileImageUrl", "introduction", null,
                null, 1L, 1L);
        when(followService.getFollowees(anyLong())).thenReturn(List.of(userInfoDTO));

        ResponseEntity<ApiResponse<List<UserInfoDTO>>> response = followController.getFollowees(principal);

        verify(followService, times(1)).getFollowees(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getBody().size());
    }
}