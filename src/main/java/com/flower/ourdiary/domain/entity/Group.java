package com.flower.ourdiary.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class Group {
    private Long seq;
    private String nick;
    private String name;
    private Integer kingUserSeq;
    private Integer userCount;
    private Date createdAt;
    private Date updatedAt;

    private User kingUser;
}
