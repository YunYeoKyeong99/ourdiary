package com.flower.ourdiary.service;

import com.flower.ourdiary.common.EmailAuthResult;
import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.domain.entity.EmailAuth;
import com.flower.ourdiary.domain.entity.UserAuth;
import com.flower.ourdiary.domain.mappedenum.EmailAuthType;
import com.flower.ourdiary.domain.mappedenum.UserAuthType;
import com.flower.ourdiary.repository.EmailAuthRepository;
import com.flower.ourdiary.repository.UserAuthRepository;
import com.flower.ourdiary.security.AutoPwEncrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final JavaMailSender emailSender;
    private final EmailAuthRepository emailAuthRepository;
    private final UserAuthRepository userAuthRepository;

    @Value("${spring.mail.username}")
    private String emailAddress;

    @Value("${server.domain}")
    private String serverDomain;

    @AutoPwEncrypt
    public void sendJoinEmail(UserAuth userAuth) {
        EmailAuth emailAuth = new EmailAuth();
        emailAuth.setType(EmailAuthType.JOIN);
        emailAuth.setId(userAuth.getId());
        emailAuth.setPw(userAuth.getPw());
        emailAuth.setUserSeq(userAuth.getUserSeq());

        for(int retry=0;retry<3;retry++) {
            try {
                emailAuth.setToken(
                        UUID.randomUUID().toString().replaceAll("-","")
                        + UUID.randomUUID().toString().replaceAll("-","")
                );
                int insertCount = emailAuthRepository.insertEmailAuth(emailAuth);
                if(insertCount == 1) {
                    break;
                }
            } catch (DuplicateKeyException ignored) {
            } catch (Exception e) {
                throw ResponseError.UNEXPECTED_ERROR.exception();
            }
        }

        if(emailAuth.getSeq() == null) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }

        boolean isSend = sendEmail(
                emailAuth.getId(),
                "가입인증메일",
                "인증링크 : http://" + serverDomain + "/email/auth/" + emailAuth.getToken()
        );

        if(!isSend) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }
    }

    public EmailAuthResult authEmail(String token) {
        EmailAuth emailAuth = emailAuthRepository.findEmailAuthByToken(token);

        if(emailAuth == null) {
            return EmailAuthResult.INVALID;
        }

        if(emailAuth.getType() != EmailAuthType.JOIN) {
            return EmailAuthResult.INVALID;
        }

        if(emailAuth.getUsedDt() != null) {
            return EmailAuthResult.ALREADY_SUCC;
        }

        if( new Date().getTime() - emailAuth.getCreatedAt().getTime() > emailAuth.getType().getExpiredMilis() ) {
            return EmailAuthResult.INVALID;
        }

        UserAuth userAuth = new UserAuth();
        userAuth.setType(UserAuthType.EMAIL);
        userAuth.setId(emailAuth.getId());
        userAuth.setPw(emailAuth.getPw());
        userAuth.setUserSeq(emailAuth.getUserSeq());

        int insertCount = userAuthRepository.insertUserAuth(userAuth);

        if(insertCount == 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }

        int updateCount = emailAuthRepository.updateEmailAuthUsedDtBySeq(emailAuth.getSeq());

        if(updateCount == 1) {
            emailAuthRepository.updateEmailAuthUsedDtByTypeAndId(emailAuth.getType(), emailAuth.getId());
        }

        return EmailAuthResult.SUCC;
    }

    private boolean sendEmail(String to,String title,String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailAddress);
        message.setTo(to);
        message.setSubject(title);
        message.setText(content);
        emailSender.send(message); // TODO handling throws MailException

        return true;
    }
}
