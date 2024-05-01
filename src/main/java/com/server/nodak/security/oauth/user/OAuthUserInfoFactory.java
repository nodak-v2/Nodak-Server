package com.server.nodak.security.oauth.user;

import com.server.nodak.domain.user.domain.UserProvider;
import com.server.nodak.exception.common.InternalServerErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

import static com.server.nodak.domain.user.domain.UserProvider.*;
import static lombok.AccessLevel.*;

@NoArgsConstructor(access = PRIVATE)
public class OAuthUserInfoFactory {

    public static OAuthUserInfo getOAuthUserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> new KakaoOAuthUserInfo(attributes);
            default -> throw new InternalServerErrorException();
        };
    }
}
