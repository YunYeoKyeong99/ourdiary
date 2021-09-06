package com.flower.ourdiary.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GroupMember {
    private Long seq;
    @JsonIgnore
    private Long groupSeq;
    private Long userSeq;
    private Date createdAt;

    private User user;
}
