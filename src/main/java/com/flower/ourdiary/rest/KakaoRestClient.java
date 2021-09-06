package com.flower.ourdiary.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoRestClient {

    String BASE_URL = "https://kapi.kakao.com";

    default String getUserIdByAccesTokenAndAppId(String accessToken, Long appId) {
        KakaoRestClient.ResUserByAccessToken userByAccessToken = null;

        try {
            userByAccessToken = getUserMeByAccessToken("Bearer "+accessToken).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if( userByAccessToken == null || userByAccessToken.getAppId() == null || !userByAccessToken.getAppId().equals(appId) )
            return null;

        return userByAccessToken.getId().toString();
    }

    default ResUserByAdminKey getUserByAdminKeyAndUserId(String adminKey, String kakaoUserId) {
        KakaoRestClient.ResUserByAdminKey kakaoUserInfo = null;

        try {
            kakaoUserInfo = getUserByAdminKey(
                    "KakaoAK "+adminKey,
                    "user_id",
                    kakaoUserId,
                    "[\"kakao_account.profile\",\"kakao_account.email\"]"
            ).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kakaoUserInfo;
    }

    @GET("/v1/user/access_token_info")
    Call<ResUserByAccessToken> getUserMeByAccessToken(
            @Header("Authorization") String accessToken // "Bearer {accessToken}"
    );

    @Getter
    @Setter
    class ResUserByAccessToken {
        private Long id;
        @JsonProperty("expiresInMillis")
        private Long expiresInMillis;
        @JsonProperty("appId")
        private Long appId;
    }

//    https://developers.kakao.com/docs/latest/ko/user-mgmt/rest-api

//    GET /v1/user/access_token_info HTTP/1.1
//    Host: kapi.kakao.com
//    Authorization: Bearer {access_token}
//    Content-type: application/x-www-form-urlencoded;charset=utf-8

//    HTTP/1.1 200 OK
//    {
//        "id":123456789,
//        "expiresInMillis":239036,
//        "appId":1234
//    }

    @GET("/v2/user/me")
    Call<ResUserByAdminKey> getUserByAdminKey(
            @Header("Authorization") String adminKey, // "KakaoAK ${social.kakao.adminkey}"
            @Query("target_id_type") String targetIdType, // "user_id"
            @Query("target_id") String targetId, // user id,
            @Query("property_keys") String propertyKeys
    );

    @Getter
    @Setter
    class ResUserByAdminKey {
        private Long id;
//        private Date connectedAt;
        private KakaoAccount kakaoAccount;
    }

    @Getter
    @Setter
    class KakaoAccount {
        private Boolean hasEmail;
        private Boolean emailNeedsAgreement;
        private Boolean isEmailValid; // if not emailNeedsAgreement then null
        private Boolean isEmailVerified; // if not emailNeedsAgreement then null
        private String email; // if not emailNeedsAgreement then null
        private Profile profile;
    }

    @Getter
    @Setter
    class Profile {
        private String nickname;
    }

//    https://developers.kakao.com/docs/latest/ko/user-mgmt/rest-api#req-user-info

//    GET/POST /v2/user/me HTTP/1.1
//    Host: kapi.kakao.com
//    Authorization: KakaoAK {admin_key}
//    Content-type: application/x-www-form-urlencoded;charset=utf-8

//    HTTP/1.1 200 OK
//    {
//        "id":123456789,
//        "properties":{
//            "nickname":"홍길동카톡",
//            "thumbnail_image":"http://xxx.kakao.co.kr/.../aaa.jpg",
//            "profile_image":"http://xxx.kakao.co.kr/.../bbb.jpg",
//            "custom_field1":"23",
//            "custom_field2":"여"
//            ...
//        },
//        "kakao_account": {
//            "profile_needs_agreement": false,
//            "profile": {
//            "nickname": "홍길동",
//            "thumbnail_image_url": "http://yyy.kakao.com/.../img_110x110.jpg",
//            "profile_image_url": "http://yyy.kakao.com/dn/.../img_640x640.jpg"
//        },
//        "email_needs_agreement":false,
//        "is_email_valid": true,
//        "is_email_verified": true,
//        "email": "xxxxxxx@xxxxx.com"
//        "age_range_needs_agreement":false,
//        "age_range":"20~29",
//        "birthday_needs_agreement":false,
//        "birthday":"1130",
//        "gender_needs_agreement":false,
//        "gender":"female"
//    }
//    }
}
