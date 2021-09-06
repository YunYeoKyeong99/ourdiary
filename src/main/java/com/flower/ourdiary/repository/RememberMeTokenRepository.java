package com.flower.ourdiary.repository;

import com.flower.ourdiary.domain.entity.RememberMeToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface RememberMeTokenRepository {
    int insertRememberMeToken(RememberMeToken rememberMeToken);

    RememberMeToken findRememberMeTokenBySeries(@Param("series") String series);

    int updateRememberMeToken(RememberMeToken rememberMeToken);

    int deleteRememberMeToken(@Param("userSeq") Integer userSeq);
}