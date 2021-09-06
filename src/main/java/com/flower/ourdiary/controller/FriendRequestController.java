package com.flower.ourdiary.controller;

import com.flower.ourdiary.common.FriendRequestDirection;
import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.common.SwaggerResponseError;
import com.flower.ourdiary.domain.entity.FriendRequest;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.req.ReqFriendDelete;
import com.flower.ourdiary.domain.req.ReqFriendRequestCreate;
import com.flower.ourdiary.domain.req.ReqFriendRequestUpdate;
import com.flower.ourdiary.security.SessionUser;
import com.flower.ourdiary.service.FriendRequestService;
import com.flower.ourdiary.service.FriendService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FriendRequestController {

  private final FriendRequestService friendRequestService;

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.INVALID_PAGE,
            ResponseError.INVALID_PAGE_SIZE
    })
    @ApiOperation("친구요청(MY_SEND,FRIEND_SEND) 조회")
    @GetMapping("/v1/friends/requests")
    public List<FriendRequest> getFriendRequestList(
            //type으로 친구요청 보낸거랑 받은거 둘다 조회
            @ApiIgnore @SessionUser User user,
            @RequestParam(value="direction",required = false) FriendRequestDirection direction,
            @RequestParam(value="page",required = false) Integer page,
            @RequestParam(value = "page_size",required = false) Integer pageSize
    ) {
        //로그인한 사용자 아이디 받아옴,
        return friendRequestService.getFriendRequestList(user.getSeq(),direction,page,pageSize);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.ALREADY_FRIEND,
            ResponseError.USER_NOT_EXISTS,
            ResponseError.UNEXPECTED_ERROR
    })
    @ApiOperation("친구요청")
    @PostMapping("/v1/friends/requests")
    public ResponseEntity<Void> createFriendRequest(
            @ApiIgnore @SessionUser User user,
            @RequestBody ReqFriendRequestCreate reqFriendRequestCreate
    ) {
        friendRequestService.createFriendRequest(user.getSeq(), reqFriendRequestCreate.getUserSeq());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.NO_AUTHORITY,
            ResponseError.ALREADY_ACCEPTED,
            ResponseError.ALREADY_DENIED,
            ResponseError.ALREADY_CANCELED,
            ResponseError.ALREADY_UPDATED
    })
    @ApiOperation("친구요청 수락/거절/취소")
    @PutMapping("/v1/friends/requests/{seq}")
    public ResponseEntity<Void> updateFriendRequest(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long seq,
            @RequestBody ReqFriendRequestUpdate reqFriendRequestUpdate
    ) {
        friendRequestService.updateFriendRequest(user.getSeq(), seq, reqFriendRequestUpdate.getResponse());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
