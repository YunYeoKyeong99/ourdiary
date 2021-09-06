package com.flower.ourdiary.security;

import com.flower.ourdiary.domain.entity.UserAuth;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Aspect
@Component
@RequiredArgsConstructor
public class UserAuthPwAspect {
    private Pattern BCRYPT_PATTERN = Pattern
            .compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    @Value("${security.password.keyword}")
    private String keyword;

    private final PasswordEncoder passwordEncoder;

    @Before( value = "execution(* com.flower.ourdiary.service.EmailAuthService.*(*)) " +
            "&& @annotation(com.flower.ourdiary.security.AutoPwEncrypt) " +
            "&& args(userAuth)")
    public void onAutoPwEncryptServiceBefore(UserAuth userAuth) {
        if( userAuth != null
                && !StringUtils.isEmpty(userAuth.getPw())
                && !BCRYPT_PATTERN.matcher(userAuth.getPw()).matches() ) {
            userAuth.setPw( encryptPassword(userAuth) );
        }
    }

    @Before( value = "execution(* com.flower.ourdiary.repository.UserAuthRepository.*(*)) " +
            "&& @annotation(com.flower.ourdiary.security.AutoPwEncrypt) " +
            "&& args(userAuth)")
    public void onAutoPwEncryptRepositoryBefore(UserAuth userAuth) {
        if( userAuth != null
                && !StringUtils.isEmpty(userAuth.getPw())
                && !BCRYPT_PATTERN.matcher(userAuth.getPw()).matches() ) {
            userAuth.setPw( encryptPassword(userAuth) );
        }
    }

    private String encryptPassword(UserAuth userAuth) {
        return passwordEncoder.encode( getRawPassword(userAuth.getId(), userAuth.getPw()) );
    }

    public boolean passwordMatches(String id, String pw, String encryptPw) {
        return passwordEncoder.matches(getRawPassword(id, pw), encryptPw);
    }

    private String getRawPassword(String id, String pw) {
        return id + keyword + pw;
    }
}