package com.flower.ourdiary.controller;

import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.common.SwaggerResponseError;
import com.flower.ourdiary.domain.entity.Diary;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.req.ReqDiaryCreate;
import com.flower.ourdiary.domain.req.ReqDiaryList;
import com.flower.ourdiary.domain.req.ReqDiaryUpdate;
import com.flower.ourdiary.security.SessionUser;
import com.flower.ourdiary.service.DiaryService;
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
public class DiaryController {
    private final DiaryService diaryService;

    @SwaggerResponseError({
            ResponseError.INVALID_PAGE_SIZE,
            ResponseError.INVALID_PREV_LAST_SEQ
    })
    @ApiOperation("Diary 조회 (리스트)")
    @GetMapping(value = "/v1/diaries")
    public List<Diary> getDiaries(
            @ApiIgnore @SessionUser User user,
            ReqDiaryList reqDiaryList
    ) {
        return diaryService.getDiaryList(user.getSeq(), reqDiaryList.getFilterType(), reqDiaryList.getSize(), reqDiaryList.getPrevLastDiarySeq());
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.DIARY_NOT_EXISTS,
            ResponseError.ACCESS_DENIED_DIARY
    })
    @ApiOperation("Diary 조회 (1개)")
    @GetMapping(value = "/v1/diaries/{diarySeq}")
    public Diary getDiary(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long diarySeq
    ) {
        return diaryService.getDiary(diarySeq, user.getSeq());
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.UNEXPECTED_ERROR
    })
    @ApiOperation("Diary 작성")
    @PostMapping(value = "/v1/diaries", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createDiary(
            @ApiIgnore @SessionUser User user,
            @RequestBody ReqDiaryCreate reqDiaryCreate
    ) {
        diaryService.createDiary(user.getSeq(), reqDiaryCreate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.TODO_CODE_ERROR
    })
    @ApiOperation("Diary 수정")
    @PutMapping(value = "/v1/diaries/{diarySeq}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateDiary(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long diarySeq,
            @RequestBody ReqDiaryUpdate reqDiaryUpdate
    ) {
        diaryService.updateDiary(user.getSeq(), diarySeq, reqDiaryUpdate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.DIARY_NOT_EXISTS,
            ResponseError.PERMISSION_DENIED_DIARY,
            ResponseError.ALREADY_DELETED
    })
    @ApiOperation("Diary 삭제")
    @DeleteMapping(value = "/v1/diaries/{diarySeq}")
    public ResponseEntity<Void> deleteDiary(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long diarySeq
    ) {
        diaryService.deleteDiary(diarySeq, user.getSeq());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.ACCESS_DENIED_DIARY
    })
    @ApiOperation("Diary 좋아요")
    @PostMapping(value = "/v1/diaries/{diarySeq}/likes")
    public ResponseEntity<Void> createDiaryLike(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long diarySeq
    ) {
        diaryService.createLike(diarySeq, user.getSeq());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SwaggerResponseError({
            ResponseError.BAD_REQUEST,
            ResponseError.ACCESS_DENIED_DIARY
    })
    @ApiOperation("Diary 좋아요 취소")
    @DeleteMapping(value = "/v1/diaries/{diarySeq}/likes")
    public ResponseEntity<Void> deleteDiaryLike(
            @ApiIgnore @SessionUser User user,
            @PathVariable Long diarySeq
    ) {
        diaryService.deleteLike(diarySeq, user.getSeq());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
