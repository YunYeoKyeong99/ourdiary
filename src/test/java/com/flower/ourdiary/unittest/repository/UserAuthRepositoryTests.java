package com.flower.ourdiary.unittest.repository;

import com.flower.ourdiary.domain.entity.UserAuth;
import com.flower.ourdiary.domain.mappedenum.UserAuthType;
import com.flower.ourdiary.security.UserAuthPwAspect;
import com.flower.ourdiary.repository.UserAuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Comparator;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ActiveProfiles("local")
public class UserAuthRepositoryTests {

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private UserAuthPwAspect userAuthPwAspect;

    private final Pattern BCRYPT_PATTERN = Pattern
            .compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    @Test
    void testUserAuthTypeHandler() {

        for(UserAuthType userAuthType : UserAuthType.values()) {
            final UserAuth userAuth = new UserAuth();
            userAuth.setType(userAuthType);
            userAuth.setId("test");
            userAuth.setPw("test");
            userAuth.setUserSeq(1);

            final int insertCount = userAuthRepository.insertUserAuth(userAuth);

            assertSame(insertCount, 1);
            assertNotNull(userAuth.getSeq());
            assertTrue(userAuth.getSeq() > 0);

            final UserAuth foundUserAuth = userAuthRepository.findUserAuthByTypeAndId(userAuth);

            assertSame(0, Comparator
                    .comparing(UserAuth::getSeq)
                    .thenComparing(UserAuth::getType)
                    .thenComparing(UserAuth::getId)
                    .thenComparing(UserAuth::getUserSeq)
                    .compare(userAuth, foundUserAuth));
        }
    }

    @Test
    void insertUserAuthTypeEmail() {

        final String originRawPassword = "test";

        final UserAuth userAuth = new UserAuth();
        userAuth.setType(UserAuthType.EMAIL);
        userAuth.setId("test1234@naver.com");
        userAuth.setPw(originRawPassword);
        userAuth.setUserSeq(1);

        final int insertCount = userAuthRepository.insertUserAuth(userAuth);

        assertEquals(insertCount, 1);
        assertNotNull(userAuth.getSeq());
        assertTrue(userAuth.getSeq() > 0);

        assertTrue(BCRYPT_PATTERN.matcher(userAuth.getPw()).matches());
        assertTrue(userAuthPwAspect.passwordMatches(userAuth.getId(), originRawPassword, userAuth.getPw()));

        final UserAuth foundUserAuth = userAuthRepository.findUserAuthByTypeAndId(userAuth);

        assertNotNull(foundUserAuth);

        assertEquals(0, Comparator
                .comparing(UserAuth::getSeq)
                .thenComparing(UserAuth::getType)
                .thenComparing(UserAuth::getId)
                .thenComparing(UserAuth::getUserSeq)
                .compare(userAuth, foundUserAuth));

        assertTrue(BCRYPT_PATTERN.matcher(foundUserAuth.getPw()).matches());
        assertTrue(userAuthPwAspect.passwordMatches(foundUserAuth.getId(), originRawPassword, foundUserAuth.getPw()));
    }

}
