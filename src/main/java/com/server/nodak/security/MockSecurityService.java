package com.server.nodak.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class MockSecurityService implements SecurityService{
    @Override
    public Authentication getAuthentication(String userId) {
        return null;
    }     // 삭제 예정


}
