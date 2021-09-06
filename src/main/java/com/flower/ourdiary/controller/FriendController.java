package com.flower.ourdiary.controller;

import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.common.SwaggerResponseError;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.req.*;
import com.flower.ourdiary.security.SessionUser;
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
public class FriendController {

  private final FriendService friendService;

    @SwaggerResponseError({
            ResponseError.INVALID_PAGE,
            ResponseError.INVALID_PAGE_SIZE
    })
    @ApiOperation("내 친구 조회 (리스트)")
    @GetMapping("/v1/friends")
    public List<User> getFriendList(
            //로그인한거 받아오는 처리
            @ApiIgnore @SessionUser User user,
            @RequestParam(value="query",required = false) String query,
            @RequestParam(value="page",required = false) Integer page,
            @RequestParam(value = "page_size",required = false) Integer pageSize
    ) {
        //로그인한 사용자 아이디 받아옴,
        return friendService.getFriendList(user.getSeq(),query,page,pageSize);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.FRIEND_NOT_EXISTS
    })
    @ApiOperation("내 친구 삭제")
    @DeleteMapping("/v1/friends")
    public ResponseEntity<Void> deleteFriend(
            @ApiIgnore @SessionUser User user,
            @RequestBody ReqFriendDelete reqFriendDelete
    ) {
        friendService.deleteFriend(user.getSeq(), reqFriendDelete.getFriendSeq());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
