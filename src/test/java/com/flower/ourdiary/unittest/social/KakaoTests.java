package com.flower.ourdiary.unittest.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.ourdiary.rest.KakaoRestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
//@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@Execution(ExecutionMode.CONCURRENT)
@ActiveProfiles("local")
public class KakaoTests {

    @Autowired
    KakaoRestClient kakaoRestClient;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${social.kakao.adminkey}")
    String kakaoAdminKey;

    @Test
    void socialKakaoAccessTokenTest() throws Exception {
        final String accessToken = "aPr6V-46_reRsEOKPjeJPHC0LTGo9xv3SpWzCworDKgAAAFx1h3zlg";
        KakaoRestClient.ResUserByAccessToken result = kakaoRestClient.getUserMeByAccessToken("Bearer "+accessToken).execute().body();
        System.out.println(objectMapper.writeValueAsString(result));
    }

    @Test
    void socialKakaoAdminKeyTest() throws Exception {
        System.out.println(kakaoAdminKey);
        KakaoRestClient.ResUserByAdminKey result = kakaoRestClient.getUserByAdminKey("KakaoAK "+kakaoAdminKey,"user_id","1347796013", "[\"kakao_account.profile\",\"kakao_account.email\"]").execute().body();

        System.out.println(objectMapper.writeValueAsString(result));

        // 400 {"msg":"NotRegisteredUserException","code":-101}
    }
}


//<!DOCTYPE html>
//<html>
//  <meta charset="utf-8"/>
//  <head>
//    <script src="/js/kakao-v1.38.0.min.js"></script>
//    <script>Kakao.init('04e2ff48d7adfdeaaec929e0056af031');</script>
//  </head>
//  <body>
//    <script>
//      window.onload = function() {
//        Kakao.Auth.login({
//          success: function(response) { console.log(response); },
//          fail: function(error) { console.log(error); }
//        });
//      }
//    </script>
//  </body>
//</html>