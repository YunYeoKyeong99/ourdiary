package com.flower.ourdiary.domain.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;

@Getter
@Setter
public class ReqGroupUpdate {
    @ApiModelProperty(required = false)
    private String nick;
    @ApiModelProperty(required = false)
    private String name;
    @ApiModelProperty(required = false)
    private Integer kingUserSeq;
}