package com.flower.ourdiary.domain.req;

import com.flower.ourdiary.domain.mappedenum.UserAuthType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUserSignIn {
    private UserAuthType type;
    private String id;
    private String pw;
}