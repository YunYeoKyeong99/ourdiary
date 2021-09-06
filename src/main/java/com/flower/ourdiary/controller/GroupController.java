package com.flower.ourdiary.controller;

import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.common.SwaggerResponseError;
import com.flower.ourdiary.domain.entity.Group;
import com.flower.ourdiary.domain.entity.GroupMember;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.req.ReqGroupCreate;
import com.flower.ourdiary.domain.req.ReqGroupMemberAdd;
import com.flower.ourdiary.domain.req.ReqGroupUpdate;
import com.flower.ourdiary.security.SessionUser;
import com.flower.ourdiary.service.GroupService;
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
public class GroupController {

    private final GroupService groupService;

    @SwaggerResponseError({
            ResponseError.INVALID_PAGE,
            ResponseError.INVALID_PAGE_SIZE,
            ResponseError.INVALID_PREV_LAST_SEQ
    })
    @ApiOperation("내 그룹 조회 (리스트)")
    @GetMapping("/v1/groups")
    public List<Group> getGroupList(
            @ApiIgnore @SessionUser User user,
            @RequestParam(value = "query",required = false) String query,
            @RequestParam(value = "page_size",required = false) Integer pageSize,
            @RequestParam(value = "prev_last_group_seq",required = false) Long prevLastGroupSeq
    ) {
        return groupService.getGroupList(user.getSeq(), query, pageSize, prevLastGroupSeq);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.GROUP_NOT_EXISTS,
            ResponseError.ACCESS_DENIED_GROUP
    })
    @ApiOperation("그룹 조회 (1개)")
    @GetMapping(value = "/v1/groups/{groupSeq}")
    public Group getDiary(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long groupSeq
    ) {
        return groupService.getGroup(user.getSeq(), groupSeq);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.INVALID_GROUP_MEMBER_SEQ
    })
    @ApiOperation("그룹 생성")
    @PostMapping(value = "/v1/groups", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createGroup(
            @ApiIgnore @SessionUser User user,
            @RequestBody ReqGroupCreate reqGroupCreate
    ) {
        groupService.createGroup(user.getSeq(), reqGroupCreate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.GROUP_NOT_EXISTS,
            ResponseError.PERMISSION_DENIED_GROUP,
            ResponseError.INVALID_KING_USER_SEQ
    })
    @ApiOperation("그룹 수정")
    @PutMapping(value = "/v1/groups/{groupSeq}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateGroup(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long groupSeq,
            @RequestBody ReqGroupUpdate reqGroupUpdate
    ) {
        groupService.updateGroup(user.getSeq(), groupSeq, reqGroupUpdate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.INVALID_PAGE_SIZE,
            ResponseError.INVALID_PREV_LAST_SEQ,
            ResponseError.GROUP_NOT_EXISTS,
            ResponseError.ACCESS_DENIED_GROUP
    })
    @ApiOperation("그룹 멤버 조회 (리스트)")
    @GetMapping(value = "/v1/groups/{groupSeq}/members")
    public List<GroupMember> getGroupMemberList(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long groupSeq,
            @RequestParam(value = "query",required = false) String query,
            @RequestParam(value = "page_size",required = false) Integer pageSize,
            @RequestParam(value = "prev_last_group_seq",required = false) Long prevLastGroupSeq
    ) {
        return groupService.getGroupMemberList(user.getSeq(), groupSeq, query, pageSize, prevLastGroupSeq);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.GROUP_NOT_EXISTS,
            ResponseError.PERMISSION_DENIED_GROUP,
            ResponseError.ALREADY_GROUP_MEMBER,
            ResponseError.INVALID_GROUP_MEMBER_SEQ
    })
    @ApiOperation("그룹 멤버 추가")
    @PostMapping(value = "/v1/groups/{groupSeq}/members", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addGroupMember(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long groupSeq,
            @RequestBody ReqGroupMemberAdd reqDiaryMemberAdd
    ) {
        groupService.addGroupMember(user.getSeq(), groupSeq, reqDiaryMemberAdd);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.GROUP_NOT_EXISTS,
            ResponseError.PERMISSION_DENIED_GROUP,
            ResponseError.GROUP_MEMBER_NOT_EXISTS
    })
    @ApiOperation("그룹 멤버 삭제")
    @DeleteMapping(value = "/v1/groups/{groupSeq}/members/{groupMemberSeq}")
    public ResponseEntity<Void> removeGroupMember(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long groupSeq,
            @PathVariable Long groupMemberSeq
    ) {
        groupService.removeGroupMember(user.getSeq(), groupSeq, groupMemberSeq);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.GROUP_NOT_EXISTS,
            ResponseError.LEAVE_DENIED_GROUP,
            ResponseError.GROUP_MEMBER_NOT_EXISTS
    })
    @ApiOperation("그룹 멤버 탈퇴")
    @DeleteMapping(value = "/v1/groups/{groupSeq}/members/me")
    public ResponseEntity<Void> removeGroupMember(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long groupSeq
    ) {
        groupService.removeGroupMemberMe(user.getSeq(), groupSeq);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
