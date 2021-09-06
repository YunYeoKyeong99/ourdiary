package com.flower.ourdiary.httpexception;

import com.flower.ourdiary.common.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@SuppressWarnings("unused")
public class ResponseExceptionHandler {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<ResponseException> handle(ResponseException e) {
        return new ResponseEntity<>(e, e.getResponseHttpStatus());
    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<ReqInvalidException> handle(MethodArgumentNotValidException e) {
        if(activeProfile.equals(Constant.PROFILE_LOCAL)) {
            e.printStackTrace();
        }
        ReqInvalidException e2 = new ReqInvalidException(e);
        return new ResponseEntity<>(e2, e2.getResponseHttpStatus());
    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<UnexpectedException> handle(Throwable t) {
        if(activeProfile.equals(Constant.PROFILE_LOCAL)) {
            t.printStackTrace();
        }
        UnexpectedException e = new UnexpectedException(t.getMessage());
        return new ResponseEntity<>(e, e.getResponseHttpStatus());
    }
}