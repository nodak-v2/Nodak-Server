package com.server.nodak.global.config;

import com.server.nodak.exception.common.AuthorizationException;
import com.server.nodak.exception.common.InternalServerErrorException;
import com.server.nodak.security.SecurityService;
import com.server.nodak.security.jwt.TokenProvider;
import com.server.nodak.utils.HttpServletUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final TokenProvider tokenProvider;
    private final HttpServletUtils servletUtils;
    private final SecurityService securityService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 유효성 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            Map<String, String> cookies = parseCookie(accessor.getFirstNativeHeader("Cookie"));

            String accessToken = cookies.get("AccessToken");
            String refreshToken = cookies.get("RefreshToken");
            
            if (accessToken != null && tokenProvider.validateToken(accessToken)) {
                setAuthentication(accessToken);
            } else {
                throw new AuthorizationException();
            }
        }
        return message;
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = securityService.getAuthentication(tokenProvider.getSubject(accessToken));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Map<String, String> parseCookie(String cookieString) {
        Map<String, String> cookies = new HashMap<>();

        String[] cookiePairs = cookieString.split(";\\s*");
        for (String cookiePair : cookiePairs) {
            String[] parts = cookiePair.split("=", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                cookies.put(key, value);
            } else {
                throw new InternalServerErrorException("Invalid cookie format");
            }
        }
        return cookies;
    }
}