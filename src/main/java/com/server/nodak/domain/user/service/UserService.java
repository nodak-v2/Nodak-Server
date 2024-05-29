package com.server.nodak.domain.user.service;

import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserInfoResponse getUserInfo() {
        return UserInfoResponse.of(SecurityUtils.getUser());
    }
}
