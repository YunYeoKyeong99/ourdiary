package com.flower.ourdiary.domain.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqGroupMemberAdd {
    @ApiModelProperty(required = true)
    private Integer userSeq;
}
