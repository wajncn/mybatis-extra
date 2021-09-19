package com.github.wajncn.extra.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 针对get方法为Optional做处理
 * 建议属性不要写Optional<T> 只需要重写get方法即可.
 **/
@MappedJdbcTypes(JdbcType.ARRAY)
@MappedTypes(Optional.class)
public class OptionalTypeHandler extends BaseTypeHandler<Object> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object objects, JdbcType jdbcType)
            throws SQLException {
        if (objects instanceof Optional) {
            objects = ((Optional<?>) objects).orElse(null);
        }
        preparedStatement.setObject(i, objects);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return getObjects(resultSet.getObject(s));
    }

    private Object getObjects(Object string) {
        return Optional.ofNullable(string).orElse(null);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return getObjects(resultSet.getString(i));
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return getObjects(callableStatement.getObject(i));
    }
}