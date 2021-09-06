package com.flower.ourdiary.domain.mappedenum;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(value = EmailAuthType.class)
public class EmailAuthTypeHandler implements TypeHandler<EmailAuthType> {
    @Override
    public void setParameter(PreparedStatement ps, int i, EmailAuthType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getType());
    }

    @Override
    public EmailAuthType getResult(ResultSet rs, String columnName) throws SQLException {
        return EmailAuthType.valueOf(rs.getInt(columnName));
    }

    @Override
    public EmailAuthType getResult(ResultSet rs, int columnIndex) throws SQLException {
        return EmailAuthType.valueOf(rs.getInt(columnIndex));
    }

    @Override
    public EmailAuthType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return EmailAuthType.valueOf(cs.getInt(columnIndex));
    }
}
