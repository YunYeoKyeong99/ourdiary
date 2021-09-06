package com.flower.ourdiary.unittest.service;

import com.flower.ourdiary.domain.entity.UserAuth;
import com.flower.ourdiary.domain.mappedenum.UserAuthType;
import com.flower.ourdiary.service.EmailAuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ActiveProfiles("local")
public class EmailAuthServiceTests {

    @Autowired
    EmailAuthService emailAuthService;

    @Test
    public void sendJoinEmail() {
        UserAuth userAuth = new UserAuth();
        userAuth.setType(UserAuthType.EMAIL);
        userAuth.setId("fgfg4514@naver.com");
        userAuth.setPw("test1234");
        userAuth.setUserSeq(1);

        emailAuthService.sendJoinEmail(userAuth);
    }
}
