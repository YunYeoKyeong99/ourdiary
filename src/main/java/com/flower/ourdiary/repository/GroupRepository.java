package com.flower.ourdiary.repository;

import com.flower.ourdiary.domain.entity.Group;
import com.flower.ourdiary.domain.entity.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@Mapper
public interface GroupRepository {
    List<Group> findGroupList(
            @Param("userSeq") Integer userSeq,
            @Param("name") String name,
            @Param("nick") String nick,
            @Param("limitSize") Integer limitSize,
            @Param("prevLastSeq") Long prevLastSeq
    );

    Group findGroup(@Param("seq") Long seq);

    List<GroupMember> findGroupMemberList(
            @Param("groupSeq") Long groupSeq,
            @Param("name") String name,
            @Param("nick") String nick,
            @Param("limitSize") Integer limitSize,
            @Param("prevLastSeq") Long prevLastSeq
    );

    GroupMember findGroupMemberBySeq(@Param("seq") Long seq);

    GroupMember findGroupMemberByGroupSeqAndUserSeq(
            @Param("groupSeq") Long groupSeq,
            @Param("userSeq") Integer userSeq
    );

    int insertGroup(Group group);

    int updateGroup(
            @Param("seq") Long seq,
            @Param("nick") String nick,
            @Param("name") String name,
            @Param("kingUserSeq") Integer kingUserSeq
    );

    int insertGroupMember(
            @Param("groupSeq") Long groupSeq,
            @Param("memberSeqList") Set<Integer> memberSeqList
    );

    int updateGroupUserCount(
            @Param("seq") Long seq,
            @Param("amount") Integer amount
    );

    int deleteGroupMember(@Param("seq") Long seq);
}
