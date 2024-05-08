package com.server.nodak.security;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserRole;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
public class NodakAuthentication extends UsernamePasswordAuthenticationToken implements OAuth2User {
    private User user;

    public NodakAuthentication(User user) {
        super(user, null, authorities(user.getRole()));
        this.user = user;
    }

    private static Collection<? extends GrantedAuthority> authorities(UserRole role) {
        return Set.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }
}
