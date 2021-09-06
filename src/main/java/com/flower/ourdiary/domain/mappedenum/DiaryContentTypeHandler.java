package com.flower.ourdiary.domain.mappedenum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.ourdiary.domain.entity.Diary;
import com.flower.ourdiary.util.ObjectMapperFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(value = Diary.Content.class)
@Slf4j
public class DiaryContentTypeHandler implements TypeHandler<Diary.Content> {

    //private final Logger log;
    private final ObjectMapper objectMapper = ObjectMapperFactory.createWithDateFormat();

    @Override
    public void setParameter(PreparedStatement ps, int i, Diary.Content parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            log.error("JsonNodeTypeHandler setParameter",e);
        }
    }

    @Override
    public Diary.Content getResult(ResultSet rs, String columnName) throws SQLException {
        try {
            return objectMapper.readValue(getJson(rs.getString(columnName)), Diary.Content.class);
        } catch (IOException e) {
            log.error("JsonNodeTypeHandler  getResult(ResultSet rs, String columnName)",e);
        }
        return null;
    }

    @Override
    public Diary.Content getResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return objectMapper.readValue(getJson(rs.getString(columnIndex)), Diary.Content.class);
        } catch (IOException e) {
            log.error("JsonNodeTypeHandler  getResult(ResultSet rs, int columnIndex)",e);
        }
        return null;
    }

    @Override
    public Diary.Content getResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return objectMapper.readValue(getJson(cs.getString(columnIndex)), Diary.Content.class);
        } catch (IOException e) {
            log.error("JsonNodeTypeHandler  getResult(CallableStatement cs, int columnIndex)",e);
        }
        return null;
    }

    private String getJson(String str) {
        if(StringUtils.isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        boolean openDoubleQuote=false;
        if(sb.charAt(0) == '"') {
            sb.deleteCharAt(0);
        }
        if(sb.charAt(sb.length()-1) == '"') {
            sb.deleteCharAt(sb.length() - 1);
        }
        for(int i=0;i<sb.length();i++) {
            char ch = sb.charAt(i);
            if(ch == '"') {
                openDoubleQuote = !openDoubleQuote;
            } else if (ch == '\\' && !openDoubleQuote) {
                sb.deleteCharAt(i);
            }
        }
        return sb.toString();
    }
}
