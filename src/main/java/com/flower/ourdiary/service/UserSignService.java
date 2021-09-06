package com.flower.ourdiary.service;

import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.entity.UserAuth;
import com.flower.ourdiary.domain.mappedenum.UserAuthType;
import com.flower.ourdiary.domain.req.ReqUserSignIn;
import com.flower.ourdiary.domain.req.ReqUserSignUp;
import com.flower.ourdiary.repository.UserAuthRepository;
import com.flower.ourdiary.repository.UserRepository;
import com.flower.ourdiary.rest.FacebookRestClient;
import com.flower.ourdiary.rest.KakaoRestClient;
import com.flower.ourdiary.security.UserAuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.flower.ourdiary.common.Constant.EMAIL_DOMAIN_GROUPS;
import static com.flower.ourdiary.common.Constant.EMAIL_REGEX;

@Service
@RequiredArgsConstructor
public class UserSignService {

    private final EmailAuthService emailAuthService;
    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final AuthenticationManager authenticationManager;
    private final KakaoRestClient kakaoClient;
    private final FacebookRestClient facebookClient;

    @Value("${social.kakao.appid}")
    Long kakaoAppId;
    @Value("${social.kakao.adminkey}")
    String kakaoAdminKey;

    @Value("${social.facebook.appid}")
    String facebookAppId;
    @Value("${social.facebook.secret}")
    String facebookSecret;

    public User signUp(ReqUserSignUp reqUserSignUp) {

        if(reqUserSignUp.getType() != UserAuthType.EMAIL) {
            throw ResponseError.INVALID_EMAIL.exception();
        }

        String email = reqUserSignUp.getId();

        if (!email.matches(EMAIL_REGEX)){
            throw ResponseError.INVALID_EMAIL.exception();
        }

        int atIndex = email.lastIndexOf('@');
        String emailIdWithAt = email.substring(0, atIndex+1);
        String emailDomain = email.substring(atIndex+1).toLowerCase();
        List<String> currentDomainGroup = null;

        for(List<String> domainGroup : EMAIL_DOMAIN_GROUPS) {
            if(domainGroup.contains(emailDomain)) {
                currentDomainGroup = domainGroup;
                break;
            }
        }

        if(currentDomainGroup == null) {
            throw ResponseError.INVALID_EMAIL.exception();
        }

        UserAuth userAuth = new UserAuth();
        userAuth.setType(UserAuthType.EMAIL);
        for(String domain : currentDomainGroup) {
            userAuth.setId(emailIdWithAt + domain);
            UserAuth existUserAuth = userAuthRepository.findUserAuthByTypeAndId(userAuth);
            if(existUserAuth != null) {
                throw ResponseError.EXISTS_EMAIL.exception();
            }
        }

        User user = new User();
        user.setEmail(reqUserSignUp.getId());
        user.setName(reqUserSignUp.getName());
        int insertCount = userRepository.insertUser(user);

        if(user.getSeq() == null)
            throw ResponseError.UNEXPECTED_ERROR.exception();

        userAuth.setId(reqUserSignUp.getId());
        userAuth.setPw(reqUserSignUp.getPw());
        userAuth.setUserSeq(user.getSeq());

        emailAuthService.sendJoinEmail(userAuth);

        return user;
    }

    public Authentication signIn(ReqUserSignIn reqUserSignIn) {

        UserAuthToken userAuthToken = new UserAuthToken(reqUserSignIn);

        Authentication auth = authenticationManager.authenticate(userAuthToken);

        if((auth == null || !auth.isAuthenticated()) && reqUserSignIn.getType().isSocial()) {
            if(reqUserSignIn.getType() == UserAuthType.KAKAO) kakaoSignUp(reqUserSignIn);
            else if(reqUserSignIn.getType() == UserAuthType.FACEBOOK) facebookSignUp(reqUserSignIn);
            else if(reqUserSignIn.getType() == UserAuthType.NAVER) naverSignUp(reqUserSignIn);
            else if(reqUserSignIn.getType() == UserAuthType.GOOGLE) googleSignUp(reqUserSignIn);

            auth = authenticationManager.authenticate(userAuthToken);
        }

        if(auth == null || !auth.isAuthenticated()) {
            throw ResponseError.BAD_REQUEST.exception();
        }

        return auth;
    }

    public void deleteUserAuth(User user) {
        userAuthRepository.deleteUserAuthByUserSeq(user.getSeq());
    }

    private void kakaoSignUp(ReqUserSignIn reqUserSignIn) {
        final String accessToken = reqUserSignIn.getId();

        final String kakaoUserId = kakaoClient.getUserIdByAccesTokenAndAppId(accessToken, kakaoAppId);

        if(kakaoUserId == null)
            return;

        KakaoRestClient.ResUserByAdminKey kakaoUserInfo = kakaoClient.getUserByAdminKeyAndUserId(kakaoAdminKey, kakaoUserId);

        if(kakaoUserInfo == null)
            return;

        String kakaoNickname = kakaoUserInfo.getKakaoAccount().getProfile().getNickname();
        // TODO SET NICKNAME

        User user = new User();
        int insertCount = userRepository.insertUser(user);

        if(user.getSeq() == null)
            return;

        UserAuth userAuth = new UserAuth();
        userAuth.setType(UserAuthType.KAKAO);
        userAuth.setId(kakaoUserId);
        userAuth.setUserSeq(user.getSeq());

        userAuthRepository.insertUserAuth(userAuth);
    }

    private void facebookSignUp(ReqUserSignIn reqUserSignIn) {
        final String accessToken = reqUserSignIn.getId();

        final FacebookRestClient.FacebookUserInfo facebookUserInfo =
                facebookClient.getUserInfo(accessToken, facebookAppId, facebookSecret);

        if(facebookUserInfo == null || StringUtils.isEmpty(facebookUserInfo.getId()))
            return;

        String name = facebookUserInfo.getName();

        User user = new User();
        int insertCount = userRepository.insertUser(user);

        if(user.getSeq() == null)
            return;

        UserAuth userAuth = new UserAuth();
        userAuth.setType(UserAuthType.FACEBOOK);
        userAuth.setId(facebookUserInfo.getId());
        userAuth.setUserSeq(user.getSeq());

        userAuthRepository.insertUserAuth(userAuth);
    }

    private void naverSignUp(ReqUserSignIn reqUserSignIn) {
        // TODO
    }

    private void googleSignUp(ReqUserSignIn reqUserSignIn) {
        // TODO
    }
}
