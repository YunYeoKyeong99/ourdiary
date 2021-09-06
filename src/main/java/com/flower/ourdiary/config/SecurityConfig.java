package com.flower.ourdiary.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.ourdiary.common.Constant;
import com.flower.ourdiary.log.ApiLogFilter;
import com.flower.ourdiary.repository.RememberMeTokenRepository;
import com.flower.ourdiary.repository.UserRepository;
import com.flower.ourdiary.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@SuppressWarnings("unused")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    RememberMeTokenRepository rememberMeTokenRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(userAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final RememberMeFilter rememberMeFilter = new RememberMeFilter("ourdiary", rememberMeTokenRepository, userRepository);

        http
            .csrf().disable()
            .headers().frameOptions().disable()
                .and()
            .addFilterBefore(new ApiLogFilter(activeProfile, objectMapper), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rememberMeFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers(Constant.PERMIT_ALL_PATHS).permitAll()
                .antMatchers(Constant.AUTHENTICATED_PATHS).authenticated()
                .antMatchers(Constant.ROLE_USER_PATHS).hasRole(Constant.ROLE_USER)
                .anyRequest().permitAll()
            .and()
            .formLogin().disable()
            .httpBasic().disable()
            .exceptionHandling()
                .authenticationEntryPoint(new DefaultAuthenticationEntryPoint())
                .accessDeniedHandler(new DefaultAccessDeniedHandler())
            .and()
            .logout()
            .logoutUrl("/api/v1/users/signout")
            .logoutSuccessHandler(new DefaultLogoutSuccessHandler(rememberMeTokenRepository, rememberMeFilter))
            .deleteCookies("JSESSIONID");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @SuppressWarnings("unused")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}