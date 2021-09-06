package com.flower.ourdiary.unittest.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.ourdiary.rest.FacebookRestClient;
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
public class FacebookTests {

    @Autowired
    FacebookRestClient facebookClient;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${social.facebook.appid}")
    String facebookAppId;
    
    @Value("${social.facebook.secret}")
    String facebookSecret;

    @Test
    void socialFacebookAccessTokenTest() throws Exception {
        final String accessToken = "EAAC4T6yZBFy8BAOWTZA2OpMXcVxErZCtDHm3goz9eyCCtkNi0TSW09ajeKVTKfBgfn6abZCpkXz1XecmRApvh4lc2OlG3vcwPlROljSuVpjFC2ZAb5kiMERHFFbZAxZCeaJfKMAVODUFogVUULaZBhI963M97D3IDsKTNbG9U02ZCi5TLD62UucDiD8UiY4FraXMZD";
        FacebookRestClient.FacebookUserInfo result = facebookClient.getUserInfoByAccessToken(accessToken, "id,name").execute().body();
        System.out.println(objectMapper.writeValueAsString(result));
    }

    @Test
    void socialFacebookAdminKeyTest() throws Exception {
        System.out.println(facebookSecret);
        FacebookRestClient.FacebookUserInfo result = facebookClient.getUserInfoByAdminKey("842386685881185",facebookAppId+"|"+facebookSecret, "id,name").execute().body();
        System.out.println(objectMapper.writeValueAsString(result));

        // 400 {"msg":"NotRegisteredUserException","code":-101}
    }
}
