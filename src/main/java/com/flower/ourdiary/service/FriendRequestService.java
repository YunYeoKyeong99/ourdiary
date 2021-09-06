package com.flower.ourdiary.service;

import com.flower.ourdiary.common.Constant;
import com.flower.ourdiary.common.FriendRequestDirection;
import com.flower.ourdiary.common.FriendRequestResponse;
import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.domain.entity.Friend;
import com.flower.ourdiary.domain.entity.FriendRequest;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.mappedenum.FriendRequestState;
import com.flower.ourdiary.repository.FriendRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserService userService;
    private final FriendService friendService;

    public List<FriendRequest> getFriendRequestList(Integer userSeq, FriendRequestDirection direction, Integer page, Integer pageSize){

        if(direction == null){
            throw ResponseError.BAD_REQUEST.exception();
        }

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

        Integer limitStart = (page-1)*pageSize; //계산

        return friendRequestRepository.findFriendRequestByUserSeq(userSeq,direction.name(), limitStart, pageSize);
    }

    public void createFriendRequest(Integer userSeq, Integer friendSeq){
        if (friendSeq <= 0 || userSeq.equals(friendSeq)){
            throw ResponseError.BAD_REQUEST.exception();
        }

        //유저체크
        User user = userService.getUser(friendSeq);

        //이미친구 사이인지 검사
        Friend friend = friendService.getFriend(userSeq, friendSeq);
        if(friend != null){
            throw ResponseError.ALREADY_FRIEND.exception();
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setFromUserSeq(userSeq);
        friendRequest.setToUserSeq(friendSeq);
        int insertCount = friendRequestRepository.insertFriendRequest(friendRequest);

        //기대치않은 에러처리
        if(insertCount == 0){
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }
    }

    @Transactional
    public void updateFriendRequest(Integer userSeq, Long seq, FriendRequestResponse response){

        if (seq <= 0 || response == null){
            throw ResponseError.BAD_REQUEST.exception();
        }

        FriendRequest friendRequest = friendRequestRepository.findFriendRequestBySeq(seq);

        Integer checkUserSeq = null;
        switch (response.getDirection()) {
            case MY_SEND:
                checkUserSeq = friendRequest.getFromUserSeq();
                break;
            case FRIEND_SEND:
                checkUserSeq = friendRequest.getToUserSeq();
                break;
        }

        //내 요청인지 체크한다. 남의 것이 수락됐는지 거절됐는지 알려주지 않을려고. 서버 api는 누구나 다 알 수 있기때문에 보안고려가 필요하다.
        if(!checkUserSeq.equals(userSeq)){
            throw ResponseError.NO_AUTHORITY.exception();
        }

        //요청상태에만 수락,거절이 가능하다
        switch (friendRequest.getState()){
            case ACCEPTED:
                throw ResponseError.ALREADY_ACCEPTED.exception();
            case DENIED:
                throw ResponseError.ALREADY_DENIED.exception();
            case CANCELED:
                throw ResponseError.ALREADY_CANCELED.exception();
        }

        int updateCount = friendRequestRepository.updateFriendRequestBySeqAndPrevState(
                response.getNextState(),
                seq,
                FriendRequestState.REQUESTED
        );

        //더블요청이 들어왔을때
        if(updateCount == 0){
            throw ResponseError.ALREADY_UPDATED.exception();
        }

        if(response.getNextState() == FriendRequestState.ACCEPTED) {
            friendService.addFriend(friendRequest.getFromUserSeq(), friendRequest.getToUserSeq());
        }
    }
}
