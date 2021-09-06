package com.flower.ourdiary.security;

import com.flower.ourdiary.domain.mappedenum.UserAuthType;
import com.flower.ourdiary.domain.req.ReqUserSignIn;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class UserAuthToken implements Authentication {

    private UserAuthType type;
    private String id;
    private String pw;

    public UserAuthToken(ReqUserSignIn reqUserSignIn) {
        type = reqUserSignIn.getType();
        id = reqUserSignIn.getId();
        pw = reqUserSignIn.getPw();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    public UserAuthType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    @Override
    public Object getCredentials() {
        return pw;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return id;
    }
}
