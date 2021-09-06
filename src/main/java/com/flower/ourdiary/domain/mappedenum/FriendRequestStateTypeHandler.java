package com.flower.ourdiary.domain.mappedenum;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(value = FriendRequestState.class)
public class FriendRequestStateTypeHandler implements TypeHandler<FriendRequestState> {
    //자바->DB
    @Override
    public void setParameter(PreparedStatement ps, int i, FriendRequestState parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getState());
    }

    //뒤의 나머지는 DB->자바
    @Override
    public FriendRequestState getResult(ResultSet rs, String columnName) throws SQLException {
        return FriendRequestState.valueOf(rs.getInt(columnName));
    }

    @Override
    public FriendRequestState getResult(ResultSet rs, int columnIndex) throws SQLException {
        return FriendRequestState.valueOf(rs.getInt(columnIndex));
    }

    @Override
    public FriendRequestState getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return FriendRequestState.valueOf(cs.getInt(columnIndex));
    }
}
