package com.flower.ourdiary.domain.req;

import com.flower.ourdiary.domain.entity.Diary;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedHashSet;

@Getter
@Setter
public class ReqDiaryUpdate {
    @ApiModelProperty(required = true)
    private String title;
    @ApiModelProperty(required = true)
    private Diary.Content content;
    @ApiModelProperty(required = true, example = "2020-06-01T22:00:00.000+0900")
    private Date wantedDt;
    @ApiModelProperty(required = false)
    private LinkedHashSet<Integer> friendSeqList;
    @ApiModelProperty(required = false)
    private LinkedHashSet<Long> groupSeqList;
    @ApiModelProperty(required = false)
    private LinkedHashSet<String> placeNameList;
    @ApiModelProperty(required = false)
    private LinkedHashSet<String> pictureUrlList;
}