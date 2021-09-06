package com.flower.ourdiary.domain.mappedenum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EmailAuthType {
    JOIN(1, 3 * 3600 * 1000),
    RESET_PW(2, 3 * 3600 * 1000);

    @Getter
    private final int type;
    private final long expiredMilis;

    public long getExpiredMilis() {
        return expiredMilis;
    }

    public static EmailAuthType valueOf(int type) {
        for(EmailAuthType emailAuthType : values())
            if(emailAuthType.getType() == type)
                return emailAuthType;

        return null;
    }
}
