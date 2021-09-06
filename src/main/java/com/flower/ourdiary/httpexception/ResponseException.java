package com.flower.ourdiary.httpexception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.flower.ourdiary.common.ResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE
)
public class ResponseException extends RuntimeException {
    private final ResponseError responseError;

    HttpStatus getResponseHttpStatus() {
        return responseError.getHttpStatus();
    }

    @JsonGetter
    @SuppressWarnings("unused")
    public String getError() {
        return responseError.name();
    }

    @JsonGetter
    public String getMessage() {
        return responseError.getMessage();
    }
}