package com.flower.ourdiary.httpexception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static com.flower.ourdiary.common.ResponseError.BAD_REQUEST;

@RequiredArgsConstructor
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE
)
public class ReqInvalidException extends RuntimeException {
    private final MethodArgumentNotValidException e;

    HttpStatus getResponseHttpStatus() {
        return BAD_REQUEST.getHttpStatus();
    }

    @JsonGetter
    @SuppressWarnings("unused")
    public String getError() {
        return BAD_REQUEST.name();
    }

    @JsonGetter
    public String getMessage() {
        return e.getBindingResult().getFieldErrorCount() > 0
                && e.getBindingResult().getFieldError().getField() != null
                ? e.getBindingResult().getFieldError().getField() + " is invalid"
                : e.getMessage();
    }
}