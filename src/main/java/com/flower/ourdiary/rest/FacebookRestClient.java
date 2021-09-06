package com.flower.ourdiary.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import retrofit2.Call;
import retrofit2.http.*;

public interface FacebookRestClient {

    String BASE_URL = "https://graph.facebook.com";

    default FacebookUserInfo getUserInfo(String accessToken, String appId, String secret) {
        FacebookUserInfo userInfo = null;

        try {
            userInfo = getUserInfoByAccessToken(accessToken, "id,name").execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(userInfo == null || StringUtils.isEmpty(userInfo.getId()))
            return null;

        String adminKey = appId+"|"+secret;


        try {
            userInfo = getUserInfoByAdminKey(userInfo.getId(), adminKey, "id,name").execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            userInfo = null;
        }

        if(userInfo == null || StringUtils.isEmpty(userInfo.getId()))
            return null;

        return userInfo;
    }

    @POST("/v7.0/me")
    @FormUrlEncoded
    Call<FacebookUserInfo> getUserInfoByAccessToken(
            @Field("access_token") String accessToken,
            @Field("fields") String fields
    );

//    https://developers.facebook.com/docs/graph-api/using-graph-api/

//    {
//        "id": "842386685881185",
//        "name": "Kim  JungWon",
//        "email": "dfdf4514@hanmail.net"
//    }

    @GET("/v7.0/{id}")
    Call<FacebookUserInfo> getUserInfoByAdminKey(
            @Path("id") String id,
            @Query("access_token") String adminKey,
            @Query("fields") String fields
    );

//    {
//        "name": "김정원",
//        "id": "842386685881185"
//    }

    @Getter
    @Setter
    class FacebookUserInfo {
        private String id;
        private String name;
        private String email; // Optional
    }
}
