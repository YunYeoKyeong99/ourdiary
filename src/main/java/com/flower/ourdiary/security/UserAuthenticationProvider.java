package com.flower.ourdiary.security;

import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.entity.UserAuth;
import com.flower.ourdiary.domain.mappedenum.UserAuthType;
import com.flower.ourdiary.repository.UserAuthRepository;
import com.flower.ourdiary.repository.UserRepository;
import com.flower.ourdiary.rest.FacebookRestClient;
import com.flower.ourdiary.rest.KakaoRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserAuthRepository userAuthRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserAuthPwAspect userAuthPwAspect;

    @Autowired
    KakaoRestClient kakaoClient;
    @Value("${social.kakao.appid}")
    Long kakaoAppId;

    @Autowired
    FacebookRestClient facebookClient;
    @Value("${social.facebook.appid}")
    String facebookAppId;
    @Value("${social.facebook.secret}")
    String facebookSecret;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(!(authentication instanceof UserAuthToken))
            return null;

        UserAuthToken userAuthToken = (UserAuthToken) authentication;

        if(userAuthToken.getType() == null || userAuthToken.getId() == null)
            return null;

        UserAuth userAuth = new UserAuth();
        userAuth.setType(userAuthToken.getType());
        userAuth.setId(userAuthToken.getId());
        userAuth.setPw(userAuthToken.getPw());

        UserAuthType type = userAuth.getType();

        String socialId = null;

        if(type.isSocial()) {
            socialId =
                type == UserAuthType.KAKAO ? getKakaoSocialId(userAuth) :
                type == UserAuthType.FACEBOOK ? getFacebookSocialId(userAuth) :
                type == UserAuthType.NAVER ? getNaverSocialId(userAuth) :
                type == UserAuthType.GOOGLE ? getGoogleSocialId(userAuth) : null;

            if (socialId == null)
                return new UserAuthentication();
        }

        Integer userSeq = type == UserAuthType.EMAIL
                ? getUserSeqByTypeEmail(userAuth)
                : getUserSeqByTypeAndSocialId(type, socialId);

        if(userSeq == null)
            return new UserAuthentication();

        User user = userRepository.findUserBySeq(userSeq);

        return user == null ? new UserAuthentication() : new UserAuthentication(user);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UserAuthToken.class);
    }

    private Integer getUserSeqByTypeEmail(UserAuth userAuth) {
        final String rawPassword = userAuth.getPw();

        userAuth = userAuthRepository.findUserAuthByTypeAndId(userAuth);

        if( userAuth == null
                || userAuth.getUserSeq() == null
                || !userAuthPwAspect.passwordMatches(userAuth.getId(), rawPassword, userAuth.getPw()))
            return null;

        return userAuth.getUserSeq();
    }

    private String getKakaoSocialId(UserAuth userAuth) {
        final String accessToken = userAuth.getId();
        return kakaoClient.getUserIdByAccesTokenAndAppId(accessToken, kakaoAppId);
    }

    private String getFacebookSocialId(UserAuth userAuth) {
        final String accessToken = userAuth.getId();

        final FacebookRestClient.FacebookUserInfo facebookUserInfo = facebookClient.getUserInfo(
                accessToken,
                facebookAppId,
                facebookSecret
        );

        if(facebookUserInfo == null || StringUtils.isEmpty(facebookUserInfo.getId()))
            return null;

        return facebookUserInfo.getId();
    }

    private String getNaverSocialId(UserAuth userAuth) {
        return null; // TODO
    }

    private String getGoogleSocialId(UserAuth userAuth) {
        return null; // TODO
    }

    private Integer getUserSeqByTypeAndSocialId(UserAuthType type, String socialId) {
        UserAuth kakaoUserAuth = new UserAuth();
        kakaoUserAuth.setType(type);
        kakaoUserAuth.setId(socialId);

        kakaoUserAuth = userAuthRepository.findUserAuthByTypeAndId(kakaoUserAuth);

        if( kakaoUserAuth == null || kakaoUserAuth.getUserSeq() == null )
            return null;

        return kakaoUserAuth.getUserSeq();
    }
}
