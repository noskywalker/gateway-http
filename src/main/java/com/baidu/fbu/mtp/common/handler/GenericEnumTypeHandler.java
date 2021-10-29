package com.baidu.fbu.mtp.common.handler;

import com.baidu.fbu.mtp.common.EnumTrait;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13:44 11/04/2015.
 *
 * @author skywalker
 */
public class GenericEnumTypeHandler<T extends EnumTrait> extends TypedTypeHandler<T> {
    private Class<T> type;

    public GenericEnumTypeHandler(Class<T> type) {
        super(type);
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int i = rs.getInt(columnName);
        return convertIntToEnum(i);
    }

    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int i = rs.getInt(columnIndex);
        return convertIntToEnum(i);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int i = cs.getInt(columnIndex);
        return convertIntToEnum(i);
    }

    private T convertIntToEnum(int i) {
        for (T enumValue : type.getEnumConstants()) {
            if (enumValue.getCode() == i) {
                return enumValue;
            }
        }
        return null;
    }
}
