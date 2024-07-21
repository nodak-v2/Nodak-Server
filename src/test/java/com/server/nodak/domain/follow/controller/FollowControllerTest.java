package com.server.nodak.domain.follow.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
}