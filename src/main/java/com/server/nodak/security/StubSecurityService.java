package com.server.nodak.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

// TODO : 삭제 예정 클래스
@Component
public class StubSecurityService implements SecurityService{
    @Override
    public Authentication getAuthentication(String userId) {
        return null;
    }
}
