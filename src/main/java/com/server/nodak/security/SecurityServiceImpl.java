package com.server.nodak.security;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import static java.util.List.*;

@Component
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
    private final UserRepository userRepository;

    @Override
    public Authentication getAuthentication(String userId) {
        User user = getUser(userId);

        return createAuthentication(user);
    }

    private UsernamePasswordAuthenticationToken createAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                of(userRoleToAuthorities(user.getRole()))
        );
    }

    private User getUser(String userId) {
        long id = Long.parseLong(userId);

        return userRepository.findById(id).orElseThrow(
                InternalServerErrorException::new
        );
    }

    private GrantedAuthority userRoleToAuthorities(UserRole userRole) {
        return new SimpleGrantedAuthority(userRole.name());
    }
}
