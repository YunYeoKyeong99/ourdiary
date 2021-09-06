package com.flower.ourdiary.config;

import com.flower.ourdiary.domain.entity.User;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
@SuppressWarnings("unused")
public class SwaggerConfig {

    public final static List<String> pathPrefixSet = Arrays.asList(
            "/swagger-ui.html",
            "/webjars/springfox-swagger-ui/",
            "/swagger-resources",
            "/v2/api-docs"
    );

    @Bean
    public Docket api() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Diary API 문서")
                .version("v0.0.1")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .ignoredParameterTypes(HttpSession.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.flower.ourdiary.controller"))
                .paths(Predicates.not(PathSelectors.regex("/docs/index.html")))
                .build()
                .apiInfo(apiInfo);
    }
}
