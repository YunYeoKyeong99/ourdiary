package com.flower.ourdiary.service;

import com.flower.ourdiary.common.Constant;
import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.domain.entity.Friend;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;

    public List<User> getFriendList(Integer userSeq, String query, Integer page, Integer pageSize){
        //페이지 관련 검사
        if(page == null){
            page = Constant.DEFAULT_PAGE;
        }

        if(page<=0){
            throw ResponseError.INVALID_PAGE.exception();
        }

        //페이지 사이즈 관련 검사
        if(pageSize == null){
            pageSize = Constant.DEFAULT_PAGE_SIZE;
        }

        if(pageSize<=0){
            throw ResponseError.INVALID_PAGE_SIZE.exception();
        }

        if(StringUtils.isEmpty(query)) {
            query = null;
        }

        Integer limitStart = (page-1)*pageSize; //계산

        return friendRepository.findFriendsByNameOrNick(userSeq, query, query, limitStart, pageSize);
    }


    public Friend getFriend(Integer userSeq, Integer friendSeq){
        if (friendSeq == null || friendSeq <= 0){
            throw ResponseError.BAD_REQUEST.exception();
        }
        return friendRepository.findFriendByUserSeqAndFriendSeq(userSeq, friendSeq);
    }

    void addFriend(Integer userSeq, Integer friendSeq) {
        if (friendSeq == null || friendSeq <= 0){
            throw ResponseError.BAD_REQUEST.exception();
        }

        friendRepository.insertFriend(userSeq, friendSeq);
        friendRepository.insertFriend(friendSeq, userSeq);
    }

    public void deleteFriend(Integer userSeq, Integer friendSeq){
        if (friendSeq <= 0){
            throw ResponseError.BAD_REQUEST.exception();
        }
        int deleteCount = friendRepository.deleteFriendByUserSeqAndFriendSeq(userSeq,friendSeq);
        if (deleteCount==0){
            throw ResponseError.FRIEND_NOT_EXISTS.exception();
        }
        friendRepository.deleteFriendByUserSeqAndFriendSeq(friendSeq,userSeq);
    }

}
