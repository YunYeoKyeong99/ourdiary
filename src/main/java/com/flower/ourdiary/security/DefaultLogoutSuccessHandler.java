package com.flower.ourdiary.security;

import com.flower.ourdiary.common.Constant;
import com.flower.ourdiary.repository.RememberMeTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class DefaultLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RememberMeTokenRepository rememberMeTokenRepository;
    private final RememberMeFilter rememberMeFilter;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String userSeqStr = MDC.get(Constant.MDC_KEY_USER_SEQ);
        Integer userSeq = null;
        if(!StringUtils.isEmpty(userSeqStr)) {
            userSeq = Integer.parseInt(userSeqStr);
        }
        rememberMeFilter.logout(request, response, userSeq);

        response.setStatus(HttpStatus.OK.value());
        response.getWriter().flush();
    }
}
