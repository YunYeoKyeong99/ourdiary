package com.flower.ourdiary.common;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public interface Constant {
    String PROFILE_LOCAL = "local";

    String MDC_KEY_THREAD_ID = "THREAD_ID";
    String MDC_KEY_USER_SEQ = "USER_SEQ";

    String ROLE_USER = "USER";
    Collection<SimpleGrantedAuthority> USER_AUTHORITIES = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + ROLE_USER));

    String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    List<List<String>> EMAIL_DOMAIN_GROUPS = Arrays.asList(
            Arrays.asList("gmail.com"),
            Arrays.asList("naver.com"),
            Arrays.asList("daum.net", "hanmail.net"),
            Arrays.asList("icloud.com", "me.com", "mac.com")
    );

    Integer DEFAULT_PAGE = 1;
    Integer DEFAULT_PAGE_SIZE = 15;

    String DEFAULT_TIME_ZONE = "Asia/Seoul";
    String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    String[] PERMIT_ALL_PATHS = new String[]{
            "/health",
            "/api/*/users/signin",
            "/api/*/users/signup",
            "/api/*/users/signout"
    };

    String[] AUTHENTICATED_PATHS = new String[]{
            "/api/*/users/me"
    };

    String[] ROLE_USER_PATHS = new String[]{
            "/api/**"
    };

    String IMAGE_URL_PREFIX = "http://image.ourdiary.site/res/images";
}
