package com.flower.ourdiary.domain.entity;

import com.flower.ourdiary.domain.mappedenum.EmailAuthType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EmailAuth {
    private Long seq;
    private EmailAuthType type;
    private Integer userSeq;
    private String token;
    private String id;
    private String pw;
    private Date usedDt;
    private Date createdAt;
}