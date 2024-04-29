package com.server.nodak.security;

import org.springframework.security.core.Authentication;

public interface SecurityService {

    Authentication getAuthentication(String userId);
}
