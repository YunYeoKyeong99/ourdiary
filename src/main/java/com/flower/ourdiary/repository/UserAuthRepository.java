package com.flower.ourdiary.repository;

import com.flower.ourdiary.domain.entity.UserAuth;
import com.flower.ourdiary.security.AutoPwEncrypt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserAuthRepository {

    @AutoPwEncrypt
    int insertUserAuth( UserAuth userAuth );

    UserAuth findUserAuthByTypeAndId( UserAuth userAuth );

    int deleteUserAuthByUserSeq(@Param("userSeq") Integer userSeq);
}
