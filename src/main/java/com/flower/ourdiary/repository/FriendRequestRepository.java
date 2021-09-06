package com.flower.ourdiary.repository;

import com.flower.ourdiary.common.FriendRequestDirection;
import com.flower.ourdiary.domain.entity.FriendRequest;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.mappedenum.FriendRequestState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface FriendRequestRepository {

    List<FriendRequest> findFriendRequestByUserSeq(
            @Param("userSeq") Integer userSeq,
            @Param("direction") String direction,
            @Param("limitStart") Integer limitStart,
            @Param("limitCount") Integer limitCount
    );

    FriendRequest findFriendRequestBySeq(
            @Param("seq") Long seq
    );

    int insertFriendRequest(
            FriendRequest friendRequest
    );

    int updateFriendRequestBySeqAndPrevState(
            @Param("state") FriendRequestState state,
            @Param("seq") Long seq,
            @Param("prevState") FriendRequestState prevState
    );

}
