package com.server.nodak.security.oauth;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.DataNotFoundException;
import com.server.nodak.security.NodakAuthentication;
import com.server.nodak.security.oauth.user.OAuthUserInfo;
import com.server.nodak.security.oauth.user.OAuthUserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.server.nodak.domain.user.domain.User.*;

@Service
@RequiredArgsConstructor
public class OAuthServiceHandler extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuthUserInfo oAuthUserInfo = OAuthUserInfoFactory.getOAuthUserInfo(
                userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes()
        );

        if (oAuthUserInfo.getEmail().isEmpty()) {
            throw new DataNotFoundException("이메일이 제공되지 않는 소셜 로그인은 허용되지 않습니다.");
        }

        User user = getUserByEmail(oAuthUserInfo);

        return new NodakAuthentication(user);
    }

    private User getUserByEmail(OAuthUserInfo oAuthUserInfo) {
        User user = userRepository.findByEmail(oAuthUserInfo.getEmail()).orElse(null);

        if (user == null) {
            user = registerUser(oAuthUserInfo);
        } else if (user.getProvider() != oAuthUserInfo.getProvider()) {
            throw new DataNotFoundException("이미 해당 이메일로 가입된 아이디가 있습니다.");
        }

        return user;
    }

    private User registerUser(OAuthUserInfo oAuthUserInfo) {
        User user = createUser(
                oAuthUserInfo.getEmail(),
                "NO_PASS",
                UUID.randomUUID().toString(),
                oAuthUserInfo.getProvider()
        );

        return userRepository.save(user);
    }
}
