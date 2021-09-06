package com.flower.ourdiary.httpexception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.flower.ourdiary.common.ResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static com.flower.ourdiary.common.ResponseError.UNEXPECTED_ERROR;

@RequiredArgsConstructor
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE
)
public class UnexpectedException extends RuntimeException {
    private final String message;

    HttpStatus getResponseHttpStatus() {
        return UNEXPECTED_ERROR.getHttpStatus();
    }

    @JsonGetter
    @SuppressWarnings("unused")
    public String getError() {
        return UNEXPECTED_ERROR.name();
    }

    @JsonGetter
    public String getMessage() {
        return message;
    }
}