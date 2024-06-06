package com.server.nodak.security.jwt;

import com.server.nodak.security.SecurityService;
import com.server.nodak.utils.HttpServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.*;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final HttpServletUtils servletUtils;
    private final SecurityService securityService;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = getAccessToken(request);
        String refreshToken = getRefreshToken(request);

        if (accessToken != null && tokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        } else if (refreshToken != null && tokenProvider.validateToken(refreshToken)) {
            String subject = tokenProvider.getSubject(refreshToken);

            accessToken = tokenProvider.createAccessToken(subject);
            refreshToken = tokenProvider.createRefreshToken(subject);

            servletUtils.addCookie(response, "AccessToken", accessToken, (int) jwtProperties.getAccessTokenExpiration());
            servletUtils.addCookie(response, "RefreshToken", refreshToken, (int) jwtProperties.getRefreshTokenExpiration());

            setAuthentication(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = securityService.getAuthentication(tokenProvider.getSubject(accessToken));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getAccessToken(HttpServletRequest request) {
        return servletUtils.getCookie(request, "AccessToken")
                .map(Cookie::getValue)
                .orElse(null);
    }

    private String getRefreshToken(HttpServletRequest request) {
        return servletUtils.getCookie(request, "RefreshToken")
                .map(Cookie::getValue)
                .orElse(null);
    }
}
