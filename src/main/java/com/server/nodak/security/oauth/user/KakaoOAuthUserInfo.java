package com.server.nodak.security.oauth.user;

import com.server.nodak.domain.user.domain.UserProvider;

import java.util.Map;

import static com.server.nodak.domain.user.domain.UserProvider.*;

public class KakaoOAuthUserInfo extends OAuthUserInfo{
    public KakaoOAuthUserInfo(Map<String, Object> attributes) {
        super(attributes, KAKAO);
    }

    @Override
    public String getEmail() {
        return String.valueOf(((Map<String, Object>) attributes.get("kakao_account")).get("email"));
    }
}
