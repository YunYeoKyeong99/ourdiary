package com.flower.ourdiary.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.ourdiary.log.RestLogInterceptor;
import com.flower.ourdiary.rest.FacebookRestClient;
import com.flower.ourdiary.rest.KakaoRestClient;
import com.flower.ourdiary.rest.OurdiaryImageRestClient;
import com.flower.ourdiary.util.ObjectMapperFactory;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
@SuppressWarnings("unused")
public class RetrofitConfig {

    @Bean
    @SuppressWarnings("unused")
    KakaoRestClient getKakaoRestClient(RestLogInterceptor restLogInterceptor) {
        return createRestClient(KakaoRestClient.class, KakaoRestClient.BASE_URL, restLogInterceptor);
    }
    @Bean
    @SuppressWarnings("unused")
    FacebookRestClient getFacebookRestClient(RestLogInterceptor restLogInterceptor) {
        return createRestClient(FacebookRestClient.class, FacebookRestClient.BASE_URL, restLogInterceptor);
    }
    @Bean
    @SuppressWarnings("unused")
    OurdiaryImageRestClient getOurdiaryImageRestClient(RestLogInterceptor restLogInterceptor) {
        return createRestClient(OurdiaryImageRestClient.class, OurdiaryImageRestClient.BASE_URL, restLogInterceptor);
    }

    private <T> T createRestClient(Class<T> restClientClass, String baseUrl, RestLogInterceptor restLogInterceptor) {
        ObjectMapper objectMapper = ObjectMapperFactory.create();

        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .addInterceptor(restLogInterceptor)
                        .build())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .baseUrl(baseUrl)
                .build()
                .create(restClientClass);
    }
}