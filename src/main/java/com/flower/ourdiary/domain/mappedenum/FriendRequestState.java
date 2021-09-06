package com.flower.ourdiary.domain.mappedenum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FriendRequestState {
    REQUESTED(0),
    ACCEPTED(1),
    DENIED(2),
    CANCELED(3);

    @Getter
    private final int state;

    public static FriendRequestState valueOf(int state) {
        for(FriendRequestState friendRequestState : values())
            if(friendRequestState.getState() == state)
                return friendRequestState;

        return null;
    }
}