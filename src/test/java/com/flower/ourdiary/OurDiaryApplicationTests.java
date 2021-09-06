package com.flower.ourdiary;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
//@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@Execution(ExecutionMode.CONCURRENT)
@ActiveProfiles("local")
public class OurDiaryApplicationTests {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String emailAddress;

    @Test
    void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailAddress);
        message.setTo("fgfg4514@naver.com");
        message.setSubject("가입인증메일");
        message.setText("인증번호 : 1234");
        emailSender.send(message); // TODO handling throws MailException
    }

}
