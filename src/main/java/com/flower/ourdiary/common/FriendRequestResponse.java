package com.flower.ourdiary.common;

import com.flower.ourdiary.domain.mappedenum.FriendRequestState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FriendRequestResponse {
    ACCEPT(FriendRequestDirection.FRIEND_SEND, FriendRequestState.ACCEPTED),
    DENY(FriendRequestDirection.FRIEND_SEND, FriendRequestState.DENIED),
    CANCEL(FriendRequestDirection.MY_SEND, FriendRequestState.CANCELED);

    private final FriendRequestDirection direction;
    private final FriendRequestState nextState;
}
