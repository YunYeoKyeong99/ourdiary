package com.flower.ourdiary.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OurdiaryImageRestClient {

    String BASE_URL = "http://image.ourdiary.site";

    default boolean getExistImageSubUrl(String imageSubUrl) {

        try {
            return getExistImageSubUrl(new ReqExistImageSubUrl(imageSubUrl)).execute().isSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @POST("/images/check")
    Call<Void> getExistImageSubUrl(@Body ReqExistImageSubUrl reqBody);

    @RequiredArgsConstructor
    @Getter
    class ReqExistImageSubUrl {
        private final String subUrl;
    }
}