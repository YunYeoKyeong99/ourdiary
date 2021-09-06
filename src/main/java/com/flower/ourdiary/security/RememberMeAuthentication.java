package com.flower.ourdiary.security;

import com.flower.ourdiary.domain.entity.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class RememberMeAuthentication extends UserAuthentication {
    WebAuthenticationDetails details;

    public RememberMeAuthentication(User user, WebAuthenticationDetails details) {
        super(user);
        this.details = details;
    }

    @Override
    public Object getDetails() {
        return details;
    }
}
