package com.flower.ourdiary.domain.mappedenum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DiaryState {
    VISIBLE(0),
    DELETED(1);

    private final int state;

    public static DiaryState valueOf(int state) {
        for(DiaryState diaryState : values())
            if(diaryState.getState() == state)
                return diaryState;

        return null;
    }
}
