package com.server.nodak.security.oauth.handler;

import com.server.nodak.utils.HttpServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final static String REDIRECT_URI_NAME = "redirect_uri";
    private final HttpServletUtils servletUtils;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        String redirectUri = getRedirectUri(request);

        servletUtils.removeCookie(request, response, REDIRECT_URI_NAME);

        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    private String getRedirectUri(HttpServletRequest request) {
        return servletUtils.getCookie(request, REDIRECT_URI_NAME)
                .map(Cookie::getValue)
                .orElse("/");
    }
}
