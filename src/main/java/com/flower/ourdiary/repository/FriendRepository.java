package com.flower.ourdiary.repository;

import com.flower.ourdiary.domain.entity.Friend;
import com.flower.ourdiary.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface FriendRepository {

    List<User> findFriendsByNameOrNick(
            @Param("userSeq") Integer userSeq,
            @Param("name") String name,
            @Param("nick") String nick,
            @Param("limitStart") Integer limitStart,
            @Param("limitCount") Integer limitCount
    );

    Friend findFriendByUserSeqAndFriendSeq(
            @Param("userSeq") Integer userSeq,
            @Param("friendSeq") Integer friendSeq
    );

    int insertFriend(
            @Param("userSeq") Integer userSeq,
            @Param("friendSeq") Integer friendSeq
    );

    int deleteFriendByUserSeqAndFriendSeq(
            @Param("userSeq") Integer userSeq,
            @Param("friendSeq") Integer friendSeq
    );

}
