package com.flower.ourdiary.repository;

import com.flower.ourdiary.domain.entity.Diary;
import com.flower.ourdiary.domain.entity.DiaryPicture;
import com.flower.ourdiary.domain.entity.DiaryPlace;
import com.flower.ourdiary.domain.mappedenum.DiaryState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
@Mapper
public interface DiaryRepository {

    List<Diary> findDiaryList(
            @Param("userSeq") Integer userSeq,
            @Param("filterType") String filterType,
            @Param("limitSize") Integer limitSize,
            @Param("prevLastDiarySeq") Long prevLastDiarySeq
    );

    Diary findDiaryBySeq(
            @Param("seq") Long seq,
            @Param("userSeq") Integer userSeq
    );

    int insertDiary(Diary diary);

    int updateDiaryStateBySeq(
            @Param("seq") Long seq,
            @Param("userSeq") Integer userSeq,
            @Param("state") DiaryState state
    );

    int insertDiaryFriendTag(
            @Param("diarySeq") Long diarySeq,
            @Param("friendSeqList") Set<Integer> friendSeqList
    );

    int insertDiaryGroupTag(
            @Param("diarySeq") Long diarySeq,
            @Param("groupSeqList") Set<Long> groupSeqList
    );

    int insertDiaryPlace(
            @Param("diarySeq") Long diarySeq,
            @Param("placeList") List<DiaryPlace> placeList
    );

    int insertDiaryPicture(
            @Param("diarySeq") Long diarySeq,
            @Param("pictureList") List<DiaryPicture> pictureList
    );

    int updateDiary(
            @Param("seq") Long seq,
            @Param("title") String title,
            @Param("content") Diary.Content content,
            @Param("wantedDt") Date wantedDt
    );

    int updateDiaryPlaceTurn(DiaryPlace diaryPlace);

    int updateDiaryPictureTurn(DiaryPicture diaryPicture);

    int updateDiaryLikeCount(
            @Param("seq") Long seq,
            @Param("amount") Integer amount
    );

    int insertDiaryLike(
            @Param("diarySeq") Long diarySeq,
            @Param("userSeq") Integer userSeq
    );

    int deleteDiaryLike(
            @Param("diarySeq") Long diarySeq,
            @Param("userSeq") Integer userSeq
    );

    int deleteDiaryFriendTag(
            @Param("diarySeq") Long diarySeq,
            @Param("friendSeqList") Set<Integer> friendSeqList
    );

    int deleteDiaryGroupTag(
            @Param("diarySeq") Long diarySeq,
            @Param("groupSeqList") Set<Long> groupSeqList
    );

    int deleteDiaryPlace(List<DiaryPlace> placeList);

    int deleteDiaryPicture(List<DiaryPicture> pictureList);
}
