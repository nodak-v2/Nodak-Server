package com.server.nodak.security.oauth.user;

import com.server.nodak.domain.user.domain.UserProvider;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class OAuthUserInfo {
    protected Map<String, Object> attributes;
    protected UserProvider provider;

    protected OAuthUserInfo(Map<String, Object> attributes, UserProvider provider) {
        this.attributes = attributes;
        this.provider = provider;
    }

    public abstract String getEmail();

}
