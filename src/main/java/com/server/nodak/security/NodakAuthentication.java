package com.server.nodak.security;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserRole;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;

@Getter
public class NodakAuthentication extends UsernamePasswordAuthenticationToken {
    private User user;

    public NodakAuthentication(User user) {
        super(user, null, authorities(user.getRole()));
        this.user = user;
    }

    private static Collection<? extends GrantedAuthority> authorities(UserRole role) {
        return Set.of(new SimpleGrantedAuthority(role.name()));
    }
}
