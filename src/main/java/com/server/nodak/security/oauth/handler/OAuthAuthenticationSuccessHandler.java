package com.server.nodak.security.oauth.handler;

import com.server.nodak.security.jwt.JwtProperties;
import com.server.nodak.security.jwt.TokenProvider;
import com.server.nodak.security.NodakAuthentication;
import com.server.nodak.utils.HttpServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.*;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final static String REDIRECT_URI_NAME = "redirect_uri";
    private final TokenProvider tokenProvider;
    private final HttpServletUtils servletUtils;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = this.determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            return;
        }

        NodakAuthentication nodakAuthentication = parseToNodakAuthentication(authentication);

        String userId = String.valueOf(nodakAuthentication.getUser().getId());

        String accessToken = tokenProvider.createAccessToken(userId);
        String refreshToken = tokenProvider.createAccessToken(userId);

        servletUtils.putHeader(response, AUTHORIZATION, accessToken);
        servletUtils.addCookie(response, "RefreshToken", refreshToken, (int) jwtProperties.getRefreshTokenExpiration());

        this.clearAuthenticationAttributes(request, response);
        this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return getRedirectUri(request);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        servletUtils.removeCookie(request, response, REDIRECT_URI_NAME);
        servletUtils.removeCookie(request, response, "oauth2_auth_request");
    }

    private String getRedirectUri(HttpServletRequest request) {
        return servletUtils.getCookie(request, REDIRECT_URI_NAME)
                .map(Cookie::getValue)
                .orElse("/");
    }

    private NodakAuthentication parseToNodakAuthentication(Authentication authentication) {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        return (NodakAuthentication) oAuth2AuthenticationToken.getPrincipal();
    }
}
