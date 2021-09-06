package com.flower.ourdiary.service;

import com.flower.ourdiary.common.Constant;
import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.domain.entity.Friend;
import com.flower.ourdiary.domain.entity.Group;
import com.flower.ourdiary.domain.entity.GroupMember;
import com.flower.ourdiary.domain.req.ReqGroupCreate;
import com.flower.ourdiary.domain.req.ReqGroupMemberAdd;
import com.flower.ourdiary.domain.req.ReqGroupUpdate;
import com.flower.ourdiary.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final FriendService friendService;

    public List<Group> getGroupList(Integer userSeq, String query, Integer pageSize, Long prevLastGroupSeq){
        if(pageSize == null){
            pageSize = Constant.DEFAULT_PAGE_SIZE;
        }

        if(pageSize <= 0){
            throw ResponseError.INVALID_PAGE_SIZE.exception();
        }

        if(StringUtils.isEmpty(query)) {
            query = null;
        }

        if(prevLastGroupSeq != null && prevLastGroupSeq <= 0) {
            throw ResponseError.INVALID_PREV_LAST_SEQ.exception();
        }

        return groupRepository.findGroupList(userSeq, query, query, pageSize, prevLastGroupSeq);
    }

    public Group getGroup(Integer userSeq, Long seq) {
        if(seq <= 0){
            throw ResponseError.BAD_REQUEST.exception();
        }

        Group group = groupRepository.findGroup(seq);

        if(group == null) {
            throw ResponseError.GROUP_NOT_EXISTS.exception();
        }
        if(isForbidden(group, userSeq)) {
            throw ResponseError.ACCESS_DENIED_GROUP.exception();
        }

        return group;
    }

    @Transactional
    public void createGroup(Integer userSeq, ReqGroupCreate reqGroupCreate){
        if( StringUtils.isEmpty(reqGroupCreate.getNick()) ){ // TODO 정규식 체크, 비속어 포함 체크
            throw ResponseError.BAD_REQUEST.exception();
        }
        if( StringUtils.isEmpty(reqGroupCreate.getName()) ){ // TODO 정규식 체크, 비속어 포함 체크
            throw ResponseError.BAD_REQUEST.exception();
        }
        if( CollectionUtils.isEmpty(reqGroupCreate.getGroupMemberSeqList()) ){
            throw ResponseError.BAD_REQUEST.exception();
        }

        for (Integer friendSeq : reqGroupCreate.getGroupMemberSeqList()) {
            Friend friend = friendService.getFriend(userSeq, friendSeq);
            if (friend == null) {
                throw ResponseError.INVALID_GROUP_MEMBER_SEQ.exception();
            }
        }

        Group group = new Group();
        group.setKingUserSeq(userSeq);
        group.setNick(reqGroupCreate.getNick());
        group.setName(reqGroupCreate.getName());
        int insertCount = groupRepository.insertGroup(group);
        if(insertCount == 0 || group.getSeq() == null || group.getSeq() <= 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }

        insertCount = groupRepository.insertGroupMember(group.getSeq(), reqGroupCreate.getGroupMemberSeqList());
        if(insertCount != reqGroupCreate.getGroupMemberSeqList().size()) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }

        int updateCount = groupRepository.updateGroupUserCount(group.getSeq(), reqGroupCreate.getGroupMemberSeqList().size());
        if(updateCount == 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }
    }

    @Transactional
    public void updateGroup(Integer userSeq, Long seq, ReqGroupUpdate reqGroupUpdate) {
        if(seq <= 0) {
            throw ResponseError.BAD_REQUEST.exception();
        }
        if( StringUtils.isEmpty(reqGroupUpdate.getNick())
                && StringUtils.isEmpty(reqGroupUpdate.getName())
                && (reqGroupUpdate.getKingUserSeq() == null || reqGroupUpdate.getKingUserSeq() <= 0)) {
            throw ResponseError.BAD_REQUEST.exception();
        }

        if(StringUtils.isEmpty(reqGroupUpdate.getNick())) {
            reqGroupUpdate.setNick(null);
        }

        if(StringUtils.isEmpty(reqGroupUpdate.getName())) {
            reqGroupUpdate.setName(null);
        }

        if (reqGroupUpdate.getKingUserSeq() != null) {
            if(reqGroupUpdate.getKingUserSeq() <= 0) {
                throw ResponseError.BAD_REQUEST.exception();
            }
            if(friendService.getFriend(userSeq, reqGroupUpdate.getKingUserSeq()) == null) {
                throw ResponseError.INVALID_KING_USER_SEQ.exception();
            }
        }

        Group group = groupRepository.findGroup(seq);

        if(group == null) {
            throw ResponseError.GROUP_NOT_EXISTS.exception();
        }
        if(!group.getKingUserSeq().equals(userSeq)) {
            throw ResponseError.PERMISSION_DENIED_GROUP.exception();
        }

        int updateCount = groupRepository.updateGroup(seq, reqGroupUpdate.getNick(), reqGroupUpdate.getName(), reqGroupUpdate.getKingUserSeq());
        if(updateCount == 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }
    }

    public List<GroupMember> getGroupMemberList(Integer userSeq, Long groupSeq, String query, Integer pageSize, Long prevLastGroupMemberSeq) {
        if(pageSize == null){
            pageSize = Constant.DEFAULT_PAGE_SIZE;
        }

        if(pageSize <= 0){
            throw ResponseError.INVALID_PAGE_SIZE.exception();
        }

        if(StringUtils.isEmpty(query)) {
            query = null;
        }

        if(prevLastGroupMemberSeq != null && prevLastGroupMemberSeq <= 0) {
            throw ResponseError.INVALID_PREV_LAST_SEQ.exception();
        }

        Group group = groupRepository.findGroup(groupSeq);

        if(group == null) {
            throw ResponseError.GROUP_NOT_EXISTS.exception();
        }
        if(isForbidden(group, userSeq)) {
            throw ResponseError.ACCESS_DENIED_GROUP.exception();
        }

        return groupRepository.findGroupMemberList(groupSeq, query, query, pageSize, prevLastGroupMemberSeq);
    }

    @Transactional
    public void addGroupMember(Integer userSeq, Long groupSeq, ReqGroupMemberAdd reqGroupMemberAdd) {
        if(groupSeq <= 0 || reqGroupMemberAdd.getUserSeq() == null || reqGroupMemberAdd.getUserSeq() <= 0) {
            throw ResponseError.BAD_REQUEST.exception();
        }

        Group group = groupRepository.findGroup(groupSeq);

        if(group == null) {
            throw ResponseError.GROUP_NOT_EXISTS.exception();
        }
        if(!group.getKingUserSeq().equals(userSeq)) {
            throw ResponseError.PERMISSION_DENIED_GROUP.exception();
        }

        GroupMember groupMember = groupRepository.findGroupMemberByGroupSeqAndUserSeq(group.getSeq(), reqGroupMemberAdd.getUserSeq());
        if(groupMember != null) {
            throw ResponseError.ALREADY_GROUP_MEMBER.exception();
        }

        Friend friend = friendService.getFriend(userSeq, reqGroupMemberAdd.getUserSeq());
        if(friend == null) {
            throw ResponseError.INVALID_GROUP_MEMBER_SEQ.exception();
        }

        int insertCount = groupRepository.insertGroupMember(group.getSeq(), Collections.singleton(reqGroupMemberAdd.getUserSeq()));
        if(insertCount == 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }

        int updateCount = groupRepository.updateGroupUserCount(group.getSeq(), insertCount);
        if(updateCount == 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }
    }

    @Transactional
    public void removeGroupMember(Integer userSeq, Long groupSeq, Long groupMemberSeq) {
        if(groupSeq <= 0 || groupMemberSeq == null || groupMemberSeq <= 0) {
            throw ResponseError.BAD_REQUEST.exception();
        }

        Group group = groupRepository.findGroup(groupSeq);

        if(group == null) {
            throw ResponseError.GROUP_NOT_EXISTS.exception();
        }
        if(!group.getKingUserSeq().equals(userSeq)) {
            throw ResponseError.PERMISSION_DENIED_GROUP.exception();
        }

        GroupMember groupMember = groupRepository.findGroupMemberBySeq(groupMemberSeq);
        if(groupMember == null) {
            throw ResponseError.GROUP_MEMBER_NOT_EXISTS.exception();
        }
        if(!groupMember.getGroupSeq().equals(group.getSeq())) {
            throw ResponseError.GROUP_MEMBER_NOT_EXISTS.exception();
        }

        int deleteCount = groupRepository.deleteGroupMember(groupMember.getSeq());
        if(deleteCount == 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }

        int updateCount = groupRepository.updateGroupUserCount(group.getSeq(), -deleteCount);
        if(updateCount == 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }
    }

    @Transactional
    public void removeGroupMemberMe(Integer userSeq, Long groupSeq) {
        if(groupSeq <= 0) {
            throw ResponseError.BAD_REQUEST.exception();
        }

        Group group = groupRepository.findGroup(groupSeq);

        if(group == null) {
            throw ResponseError.GROUP_NOT_EXISTS.exception();
        }
        if(group.getKingUserSeq().equals(userSeq)) {
            throw ResponseError.LEAVE_DENIED_GROUP.exception();
        }

        GroupMember groupMember = groupRepository.findGroupMemberByGroupSeqAndUserSeq(groupSeq, userSeq);
        if(groupMember == null) {
            throw ResponseError.GROUP_MEMBER_NOT_EXISTS.exception();
        }

        int deleteCount = groupRepository.deleteGroupMember(groupMember.getSeq());
        if(deleteCount == 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }

        int updateCount = groupRepository.updateGroupUserCount(group.getSeq(), -deleteCount);
        if(updateCount == 0) {
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }
    }

    private boolean isForbidden(Group group, Integer userSeq) {
        return !group.getKingUserSeq().equals(userSeq)
                && groupRepository.findGroupMemberByGroupSeqAndUserSeq(group.getSeq(), userSeq) == null;
    }
}
