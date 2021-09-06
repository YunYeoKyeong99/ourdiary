package com.flower.ourdiary.domain.mappedenum;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(value = UserAuthType.class)
public class UserAuthTypeHandler implements TypeHandler<UserAuthType> {
    @Override
    public void setParameter(PreparedStatement ps, int i, UserAuthType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getType());
    }

    @Override
    public UserAuthType getResult(ResultSet rs, String columnName) throws SQLException {
        return UserAuthType.valueOf(rs.getInt(columnName));
    }

    @Override
    public UserAuthType getResult(ResultSet rs, int columnIndex) throws SQLException {
        return UserAuthType.valueOf(rs.getInt(columnIndex));
    }

    @Override
    public UserAuthType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return UserAuthType.valueOf(cs.getInt(columnIndex));
    }
}
