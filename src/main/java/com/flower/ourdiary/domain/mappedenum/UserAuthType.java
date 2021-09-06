package com.flower.ourdiary.domain.mappedenum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserAuthType {
    EMAIL(1),
    GOOGLE(2),
    NAVER(3),
    KAKAO(4),
    FACEBOOK(5);

    @Getter
    private final int type;

    public static UserAuthType valueOf(int type) {
        for(UserAuthType userAuthType : values())
            if(userAuthType.getType() == type)
                return userAuthType;

        return null;
    }

    public boolean isSocial() {
        return this != EMAIL;
    }
}