package com.flower.ourdiary.service;

import com.flower.ourdiary.common.Constant;
import com.flower.ourdiary.common.ResponseError;
import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.entity.UserFcm;
import com.flower.ourdiary.domain.req.ReqUserUpdateMe;
import com.flower.ourdiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getUserList(Integer userSeq, String query, Integer page, Integer pageSize){
        //쿼리가 비었을때 검사
        if(StringUtils.isEmpty(query)){
            throw ResponseError.INVALID_QUERY.exception();
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

        return userRepository.findUsersByNameOrNick(userSeq, query, query, limitStart, pageSize);
    }

    public User getUser(Integer userSeq) {
        User user = userRepository.findUserBySeq(userSeq);

        if(user == null) {
            throw ResponseError.USER_NOT_EXISTS.exception();
        }

        return user;
    }

    public User updateUser(Integer userSeq, ReqUserUpdateMe reqUserUpdateMe) {
        if( StringUtils.isEmpty(reqUserUpdateMe.getName())
            && StringUtils.isEmpty(reqUserUpdateMe.getNick()) ) {
            throw ResponseError.BAD_REQUEST.exception();
        }

        // TODO CHECK name, nick REGEX Valid

        User user = getUser(userSeq);

        if( !StringUtils.isEmpty(reqUserUpdateMe.getNick()) ) {
            user.setNick(reqUserUpdateMe.getNick());
            try {
                userRepository.updateUserNick(user);
            } catch (DuplicateKeyException e) {
                throw ResponseError.DUPLICATE_USER_NICK.exception();
            }
        }

        if( !StringUtils.isEmpty(reqUserUpdateMe.getName()) ) {
            user.setName(reqUserUpdateMe.getName());
            userRepository.updateUser(user);
        }

        return user;
    }

    public void updateUserFcm(Integer userSeq, String token) {
        if(StringUtils.isEmpty(token)) {
            throw ResponseError.BAD_REQUEST.exception();
        }

        UserFcm userFcm = new UserFcm();
        userFcm.setUserSeq(userSeq);
        userFcm.setToken(token);
        userRepository.upsertUserFcm(userFcm);
    }

    public void deleteUser(User user) {
        userRepository.deleteUser(user);
    }
}
