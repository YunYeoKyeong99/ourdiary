package com.flower.ourdiary.security;

import com.flower.ourdiary.common.ResponseError;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
        response.sendError(
                ResponseError.UNAUTHORIZED.getHttpStatus().value(),
                ResponseError.UNAUTHORIZED.getMessage());
    }
}