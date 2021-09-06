package com.flower.ourdiary.service;

import com.flower.ourdiary.common.Constant;
import com.flower.ourdiary.common.DiaryListFilterType;
import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.domain.entity.*;
import com.flower.ourdiary.domain.mappedenum.DiaryState;
import com.flower.ourdiary.domain.req.ReqDiaryCreate;
import com.flower.ourdiary.domain.req.ReqDiaryUpdate;
import com.flower.ourdiary.repository.DiaryRepository;
import com.flower.ourdiary.rest.OurdiaryImageRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final FriendService friendService;
    private final GroupService groupService;
    private final OurdiaryImageRestClient ourdiaryImageRestClient;

    public List<Diary> getDiaryList(Integer userSeq, DiaryListFilterType filterType, Integer pageSize, Long prevLastDiarySeq){
        //페이지 사이즈 관련 검사
        if(pageSize == null){
            pageSize = Constant.DEFAULT_PAGE_SIZE;
        }

        if(pageSize<=0){
            throw ResponseError.INVALID_PAGE_SIZE.exception();
        }

        if(prevLastDiarySeq != null && prevLastDiarySeq <= 0) {
            throw ResponseError.INVALID_PREV_LAST_SEQ.exception();
        }

        return diaryRepository.findDiaryList(userSeq, filterType.toString(), pageSize, prevLastDiarySeq);
    }

    public Diary getDiary(Long seq, Integer userSeq) {
        if(seq <= 0){
            throw ResponseError.BAD_REQUEST.exception();
        }

        Diary diary = diaryRepository.findDiaryBySeq(seq, userSeq);

        if(diary == null) {
            throw ResponseError.DIARY_NOT_EXISTS.exception();
        }
        if(diary.getIsForbidden()) {
            throw ResponseError.ACCESS_DENIED_DIARY.exception();
        }

        return diary;
    }

    @Transactional
    public void createDiary(Integer userSeq, ReqDiaryCreate reqDiaryCreate){
        if( StringUtils.isEmpty(reqDiaryCreate.getTitle())){ // TODO 정규식 체크
            throw ResponseError.INVALID_TITLE.exception();
        }
        if( StringUtils.isEmpty(reqDiaryCreate.getContent())){ // TODO 비속어 포함 체크
            throw ResponseError.INVALID_CONTENT.exception();
        }
        if( reqDiaryCreate.getWantedDt() == null){ // TODO CHECK 최소날짜 최대날짜
            throw ResponseError.INVALID_WANTED_DT.exception();
        }
        if(!CollectionUtils.isEmpty(reqDiaryCreate.getFriendSeqList())) {
            for (Integer friendSeq : reqDiaryCreate.getFriendSeqList()) {
                if(friendSeq == null || friendSeq <= 0) {
                    throw ResponseError.INVALID_FRIEND_SEQ.exception();
                }
                Friend friend = friendService.getFriend(userSeq, friendSeq);
                if (friend == null) {
                    throw ResponseError.INVALID_FRIEND_SEQ.exception();
                }
            }
        }
        if(!CollectionUtils.isEmpty(reqDiaryCreate.getGroupSeqList())) {
            for (Long groupSeq : reqDiaryCreate.getGroupSeqList()) {
                if (groupSeq == null || groupSeq <= 0) {
                    throw ResponseError.INVALID_GROUP_SEQ.exception();
                }
                Group group = groupService.getGroup(userSeq, groupSeq);
            }
        }
        if(!CollectionUtils.isEmpty(reqDiaryCreate.getPlaceNameList())) {
            for (String placeName : reqDiaryCreate.getPlaceNameList()) {
                if (StringUtils.isEmpty(placeName)) { // TODO 정규식 체크
                    throw ResponseError.INVALID_PLACE_NAME.exception();
                }
            }
        }
        if(!CollectionUtils.isEmpty(reqDiaryCreate.getPictureUrlList())) {
            for (String pictureUrl : reqDiaryCreate.getPictureUrlList()) {
                if (StringUtils.isEmpty(pictureUrl)) {
                    throw ResponseError.INVALID_PICTURE_URL.exception();
                }
                if (!pictureUrl.startsWith(Constant.IMAGE_URL_PREFIX)) {
                    throw ResponseError.INVALID_PICTURE_URL_PREFIX.exception();
                }
                final String imageSubUrl = pictureUrl.substring(Constant.IMAGE_URL_PREFIX.length());
                if (!ourdiaryImageRestClient.getExistImageSubUrl(imageSubUrl)) {
                    throw ResponseError.NOT_EXISTS_PICTURE_URL.exception();
                }
            }
        }

        Diary diary = new Diary();
        diary.setUserSeq(userSeq);
        diary.setTitle(reqDiaryCreate.getTitle());
        diary.setContent(reqDiaryCreate.getContent());
        diary.setWantedDt(reqDiaryCreate.getWantedDt());
        int insertCount = diaryRepository.insertDiary(diary);
        if(insertCount == 0 || diary.getSeq() == null || diary.getSeq() <= 0){
            throw ResponseError.UNEXPECTED_ERROR.exception();
        }
        if(!CollectionUtils.isEmpty(reqDiaryCreate.getFriendSeqList())){
            insertCount = diaryRepository.insertDiaryFriendTag(diary.getSeq(), reqDiaryCreate.getFriendSeqList());
            if(insertCount != reqDiaryCreate.getFriendSeqList().size()){
                throw ResponseError.UNEXPECTED_ERROR.exception();
            }
        }
        if(!CollectionUtils.isEmpty(reqDiaryCreate.getGroupSeqList())){
            insertCount = diaryRepository.insertDiaryGroupTag(diary.getSeq(), reqDiaryCreate.getGroupSeqList());
            if(insertCount != reqDiaryCreate.getGroupSeqList().size()){
                throw ResponseError.UNEXPECTED_ERROR.exception();
            }
        }
        if(!CollectionUtils.isEmpty(reqDiaryCreate.getPlaceNameList())) {
            List<DiaryPlace> diaryPlaceList = new LinkedList<>();
            for(String placeName : reqDiaryCreate.getPlaceNameList()) {
                DiaryPlace diaryPlace = new DiaryPlace();
                diaryPlace.setName(placeName);
                diaryPlace.setTurn(diaryPlaceList.size());
            }
            insertCount = diaryRepository.insertDiaryPlace(diary.getSeq(), diaryPlaceList);
            if (insertCount != reqDiaryCreate.getPlaceNameList().size()) {
                throw ResponseError.UNEXPECTED_ERROR.exception();
            }
        }
        if(!CollectionUtils.isEmpty(reqDiaryCreate.getPictureUrlList())) {
            List<DiaryPicture> diaryPictureList = new LinkedList<>();
            for(String pictureUrl : reqDiaryCreate.getPictureUrlList()) {
                DiaryPicture diaryPicture = new DiaryPicture();
                diaryPicture.setPath(pictureUrl);
                diaryPicture.setTurn(diaryPictureList.size());
            }
            insertCount = diaryRepository.insertDiaryPicture(diary.getSeq(), diaryPictureList);
            if (insertCount != reqDiaryCreate.getPictureUrlList().size()) {
                throw ResponseError.UNEXPECTED_ERROR.exception();
            }
        }
    }

    @Transactional
    public void updateDiary(Integer userSeq, Long seq, ReqDiaryUpdate reqDiaryUpdate) {
        if(seq <= 0
            || (reqDiaryUpdate.getFriendSeqList() != null && reqDiaryUpdate.getFriendSeqList().stream().anyMatch(f -> f == null || f <= 0))
            || (reqDiaryUpdate.getGroupSeqList() != null && reqDiaryUpdate.getGroupSeqList().stream().anyMatch(g -> g == null || g <= 0))
            || (reqDiaryUpdate.getPlaceNameList() != null && reqDiaryUpdate.getPlaceNameList().stream().anyMatch(StringUtils::isEmpty))
            || (reqDiaryUpdate.getPictureUrlList() != null && reqDiaryUpdate.getPictureUrlList().stream().anyMatch(StringUtils::isEmpty))
        ){
            throw ResponseError.BAD_REQUEST.exception();
        }

        Diary diary = diaryRepository.findDiaryBySeq(seq, userSeq);

        if(diary == null) {
            throw ResponseError.DIARY_NOT_EXISTS.exception();
        }
        if(!diary.getUserSeq().equals(userSeq)) {
            throw ResponseError.PERMISSION_DENIED_DIARY.exception();
        }

        String updateTitle = null;
        if(!StringUtils.isEmpty(reqDiaryUpdate.getTitle()) && !reqDiaryUpdate.getTitle().equals(diary.getTitle())) {
            updateTitle = reqDiaryUpdate.getTitle();
        }
        Diary.Content updateContent = null;
        if(!StringUtils.isEmpty(reqDiaryUpdate.getContent()) && !reqDiaryUpdate.getContent().getText().equals(diary.getContent().getText())) {
            updateContent = reqDiaryUpdate.getContent();
        }
        Date updateWantedDt = null;
        if(reqDiaryUpdate.getWantedDt() != null && !reqDiaryUpdate.getWantedDt().equals(diary.getWantedDt())) {
            updateWantedDt = reqDiaryUpdate.getWantedDt();
        }

        LinkedHashSet<Integer> insertDiaryFriendSeqList = new LinkedHashSet<>();
        TreeSet<Integer> deleteDiaryFriendSeqTreeSet = null;
        if(reqDiaryUpdate.getFriendSeqList() != null) {
            deleteDiaryFriendSeqTreeSet = diary.getFriendList().stream().map(User::getSeq).collect(Collectors.toCollection(TreeSet::new));
            for(Integer friendSeq : reqDiaryUpdate.getFriendSeqList()) {
                Friend friend = friendService.getFriend(userSeq, friendSeq);
                if(friend == null) {
                    throw ResponseError.INVALID_FRIEND_SEQ.exception();
                }
                if(!deleteDiaryFriendSeqTreeSet.remove(friendSeq)) {
                    insertDiaryFriendSeqList.add(friendSeq);
                }
            }
        }

        LinkedHashSet<Long> insertDiaryGroupSeqList = new LinkedHashSet<>();
        TreeSet<Long> deleteDiaryGroupSeqTreeSet = null;
        if(reqDiaryUpdate.getGroupSeqList() != null) {
            deleteDiaryGroupSeqTreeSet = diary.getGroupList().stream().map(Group::getSeq).collect(Collectors.toCollection(TreeSet::new));
            for(Long groupSeq : reqDiaryUpdate.getGroupSeqList()) {
                if (groupSeq == null || groupSeq <= 0) {
                    throw ResponseError.INVALID_GROUP_SEQ.exception();
                }
                Group group = groupService.getGroup(userSeq, groupSeq);
            }
        }

        List<DiaryPlace> insertDiaryPlaceList = new LinkedList<>();
        List<DiaryPlace> updateDiaryPlaceTurnList = new LinkedList<>();
        List<DiaryPlace> deleteDiaryPlaceList = new LinkedList<>();
        if(reqDiaryUpdate.getPlaceNameList() != null) {
            LinkedHashSet<String> targetPlaceNameSet = new LinkedHashSet<>(reqDiaryUpdate.getPlaceNameList());
            ArrayList<String> targetPlaceNameList = new ArrayList<>(reqDiaryUpdate.getPlaceNameList());
            for(DiaryPlace diaryPlace : diary.getPlaceList()) {
                boolean isKeep = targetPlaceNameSet.remove(diaryPlace.getName());
                if(isKeep) {
                    diaryPlace.setTurn(targetPlaceNameList.indexOf(diaryPlace.getName()));
                    updateDiaryPlaceTurnList.add(diaryPlace);
                } else {
                    deleteDiaryPlaceList.add(diaryPlace);
                }
            }
            for(String placeName : targetPlaceNameSet) {
                DiaryPlace diaryPlace = new DiaryPlace();
                diaryPlace.setName(placeName);
                diaryPlace.setTurn(targetPlaceNameList.indexOf(placeName));
                insertDiaryPlaceList.add(diaryPlace);
            }
        }

        List<DiaryPicture> insertDiaryPictureList = new LinkedList<>();
        List<DiaryPicture> updateDiaryPictureTurnList = new LinkedList<>();
        List<DiaryPicture> deleteDiaryPictureList = new LinkedList<>();
        if(reqDiaryUpdate.getPictureUrlList() != null) {
            LinkedHashSet<String> targetPictureUrlSet = new LinkedHashSet<>(reqDiaryUpdate.getPictureUrlList());
            ArrayList<String> targetPictureUrlList = new ArrayList<>(reqDiaryUpdate.getPictureUrlList());
            for(DiaryPicture diaryPicture : diary.getPictureList()) {
                boolean isKeep = targetPictureUrlSet.remove(diaryPicture.getPath());
                if(isKeep) {
                    diaryPicture.setTurn(targetPictureUrlList.indexOf(diaryPicture.getPath()));
                    updateDiaryPictureTurnList.add(diaryPicture);
                } else {
                    deleteDiaryPictureList.add(diaryPicture);
                }
            }
            for(String pictureUrl : targetPictureUrlSet) {
                DiaryPicture diaryPicture = new DiaryPicture();
                diaryPicture.setPath(pictureUrl);
                diaryPicture.setTurn(targetPictureUrlList.indexOf(pictureUrl));
                insertDiaryPictureList.add(diaryPicture);
            }
        }

        if(updateTitle != null || updateContent != null || updateWantedDt != null) {
            int updateCount = diaryRepository.updateDiary(seq, updateTitle, updateContent, updateWantedDt);
        }

        if(!CollectionUtils.isEmpty(insertDiaryFriendSeqList)) {
            diaryRepository.insertDiaryFriendTag(diary.getSeq(), insertDiaryFriendSeqList);
        }
        if(!CollectionUtils.isEmpty(deleteDiaryFriendSeqTreeSet)) {
            diaryRepository.deleteDiaryFriendTag(diary.getSeq(), deleteDiaryFriendSeqTreeSet);
        }

        if(!CollectionUtils.isEmpty(insertDiaryGroupSeqList)) {
            diaryRepository.insertDiaryGroupTag(diary.getSeq(), insertDiaryGroupSeqList);
        }
        if(!CollectionUtils.isEmpty(deleteDiaryGroupSeqTreeSet)) {
            diaryRepository.deleteDiaryGroupTag(diary.getSeq(), deleteDiaryGroupSeqTreeSet);
        }

        if(!CollectionUtils.isEmpty(insertDiaryPlaceList)) {
            diaryRepository.insertDiaryPlace(diary.getSeq(), insertDiaryPlaceList);
        }
        if(!CollectionUtils.isEmpty(updateDiaryPlaceTurnList)) {
            for(DiaryPlace diaryPlace : updateDiaryPlaceTurnList) {
                diaryRepository.updateDiaryPlaceTurn(diaryPlace);
            }
        }
        if(!CollectionUtils.isEmpty(deleteDiaryPlaceList)) {
            diaryRepository.deleteDiaryPlace(deleteDiaryPlaceList);
        }

        if(!CollectionUtils.isEmpty(insertDiaryPictureList)) {
            diaryRepository.insertDiaryPicture(diary.getSeq(), insertDiaryPictureList);
        }
        if(!CollectionUtils.isEmpty(updateDiaryPictureTurnList)) {
            for(DiaryPicture diaryPicture : updateDiaryPictureTurnList) {
                diaryRepository.updateDiaryPictureTurn(diaryPicture);
            }
        }
        if(!CollectionUtils.isEmpty(deleteDiaryPictureList)) {
            diaryRepository.deleteDiaryPicture(deleteDiaryPictureList);
        }
    }

    public void deleteDiary(Long seq, Integer userSeq) {
        if(seq <= 0){
            throw ResponseError.BAD_REQUEST.exception();
        }

        Diary diary = diaryRepository.findDiaryBySeq(seq, userSeq);

        if(diary == null) {
            throw ResponseError.DIARY_NOT_EXISTS.exception();
        }
        if(!diary.getUserSeq().equals(userSeq)) {
            throw ResponseError.PERMISSION_DENIED_DIARY.exception();
        }

        int updateCount = diaryRepository.updateDiaryStateBySeq(seq, userSeq, DiaryState.DELETED);
        if(updateCount == 0) {
            throw ResponseError.ALREADY_DELETED.exception();
        }
    }

    @Transactional
    public void createLike(Long seq, Integer userSeq){
        if(seq <= 0){
            throw ResponseError.BAD_REQUEST.exception();
        }

        Diary diary = diaryRepository.findDiaryBySeq(seq, userSeq);
        if(diary == null){
            throw ResponseError.BAD_REQUEST.exception();
        }
        if(diary.getIsForbidden()) {
            throw ResponseError.ACCESS_DENIED_DIARY.exception();
        }

        int insertCount = 0;
        try {
            insertCount = diaryRepository.insertDiaryLike(seq, userSeq);
        } catch (DuplicateKeyException ignored) { }

        if(insertCount == 1){
            diaryRepository.updateDiaryLikeCount(seq, insertCount);
        }
    }

    @Transactional
    public void deleteLike(Long seq, Integer userSeq){
        if(seq <= 0){
            throw ResponseError.BAD_REQUEST.exception();
        }

        Diary diary = diaryRepository.findDiaryBySeq(seq, userSeq);
        if(diary == null){
            throw ResponseError.BAD_REQUEST.exception();
        }
        if(diary.getIsForbidden()) {
            throw ResponseError.ACCESS_DENIED_DIARY.exception();
        }

        int insertCount = 0;
        try {
            insertCount = diaryRepository.deleteDiaryLike(seq, userSeq);
        } catch (DuplicateKeyException ignored) { }

        if(insertCount == 1){
            diaryRepository.updateDiaryLikeCount(seq, -insertCount);
        }
    }
}
