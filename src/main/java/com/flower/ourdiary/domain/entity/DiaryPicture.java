package com.flower.ourdiary.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
public class DiaryPicture implements Comparable<DiaryPicture> {
    private Long seq;
    private Long diarySeq;
    private String path;
    private Integer turn;

    @Override
    public int compareTo(DiaryPicture o) {
        return ( !diarySeq.equals(o.getDiarySeq()) ? Comparator.comparingLong(DiaryPicture::getDiarySeq)
                : !turn.equals(o.getTurn()) ? Comparator.comparingLong(DiaryPicture::getTurn)
                : Comparator.comparingLong(DiaryPicture::getSeq)
        ).compare(this, o);
    }
}
