package com.flower.ourdiary.domain.req;

import com.flower.ourdiary.common.DiaryListFilterType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqDiaryList {
    @ApiModelProperty(required = true, example = "ALL")
    private DiaryListFilterType filterType = DiaryListFilterType.ALL;
    @ApiModelProperty(required = false, example = "15")
    private Integer size;
    @ApiModelProperty(required = false)
    private Long prevLastDiarySeq;
}
