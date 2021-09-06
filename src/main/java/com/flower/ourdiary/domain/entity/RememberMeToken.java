package com.flower.ourdiary.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class RememberMeToken {
    private final int userSeq;
    private final String series;
    private final String token;
    private final Date lastUsed;
}
