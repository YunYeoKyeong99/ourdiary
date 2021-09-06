package com.flower.ourdiary.domain.mappedenum;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(value = DiaryState.class)
public class DiaryStateTypeHandler implements TypeHandler<DiaryState> {
    @Override
    public void setParameter(PreparedStatement ps, int i, DiaryState parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getState());
    }

    @Override
    public DiaryState getResult(ResultSet rs, String columnName) throws SQLException {
        return DiaryState.valueOf(rs.getInt(columnName));
    }

    @Override
    public DiaryState getResult(ResultSet rs, int columnIndex) throws SQLException {
        return DiaryState.valueOf(rs.getInt(columnIndex));
    }

    @Override
    public DiaryState getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return DiaryState.valueOf(cs.getInt(columnIndex));
    }
}
