package com.flower.ourdiary.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
public class DiaryPlace implements Comparable<DiaryPlace> {
    private Long seq;
    private Long diarySeq;
    private String name;
    private Integer turn;

    @Override
    public int compareTo(DiaryPlace o) {
        return ( !diarySeq.equals(o.getDiarySeq()) ? Comparator.comparingLong(DiaryPlace::getDiarySeq)
                : !turn.equals(o.getTurn()) ? Comparator.comparingLong(DiaryPlace::getTurn)
                : Comparator.comparingLong(DiaryPlace::getSeq)
        ).compare(this, o);
    }
}