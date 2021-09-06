package com.flower.ourdiary.security;

import com.flower.ourdiary.common.Constant;
import com.flower.ourdiary.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;

public class UserAuthentication implements Authentication {

    User user;

    public UserAuthentication() {
    }

    public UserAuthentication(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return StringUtils.isEmpty(user.getNick()) || StringUtils.isEmpty(user.getName())
            ? Collections.emptyList()
            : Constant.USER_AUTHORITIES;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return user != null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

    @Override
    public String getName() {
        return null;
    }
}
