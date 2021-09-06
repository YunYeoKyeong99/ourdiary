package com.flower.ourdiary.repository;

import com.flower.ourdiary.domain.entity.User;
import com.flower.ourdiary.domain.entity.UserFcm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserRepository {

    int insertUser(User user);

    List<User> findUsersByNameOrNick(
            @Param("userSeq") Integer userSeq,
            @Param("name") String name,
            @Param("nick") String nick,
            @Param("limitStart") Integer limitStart,
            @Param("limitCount") Integer limitCount
        );

    User findUserBySeq(Integer Seq);

    int updateUser(User user);

    int updateUserNick(User user);

    int upsertUserFcm(UserFcm userFcm);

    int deleteUser(User user);
}
