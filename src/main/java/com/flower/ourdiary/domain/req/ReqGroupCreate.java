package com.flower.ourdiary.domain.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;

@Getter
@Setter
public class ReqGroupCreate {
    @ApiModelProperty(required = true)
    private String nick;
    @ApiModelProperty(required = true)
    private String name;
    @ApiModelProperty(required = true)
    private LinkedHashSet<Integer> groupMemberSeqList;
}