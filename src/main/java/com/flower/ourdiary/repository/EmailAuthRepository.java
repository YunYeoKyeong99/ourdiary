package com.flower.ourdiary.repository;

import com.flower.ourdiary.domain.entity.EmailAuth;
import com.flower.ourdiary.domain.mappedenum.EmailAuthType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface EmailAuthRepository {

    int insertEmailAuth(EmailAuth emailAuth);

    EmailAuth findEmailAuthByToken(@Param("token") String token);

    int updateEmailAuthUsedDtBySeq(@Param("seq") Long seq);

    int updateEmailAuthUsedDtByTypeAndId(
            @Param("type") EmailAuthType type,
            @Param("id") String id
    );
}
