package com.flower.ourdiary.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flower.ourdiary.domain.mappedenum.FriendRequestState;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FriendRequest {
    private Long seq;
    @JsonIgnore
    private Integer fromUserSeq;
    @JsonIgnore
    private Integer toUserSeq;
    private FriendRequestState state;
    private Date createdAt;

    private User user;
}
