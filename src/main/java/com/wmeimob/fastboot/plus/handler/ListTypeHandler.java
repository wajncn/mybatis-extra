package com.wmeimob.fastboot.plus.handler;

import com.wmeimob.fastboot.plus.util.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理List<T>
 * 数据库格式: [123,444]
 * 数据库格式: ["123","4444"]
 *
 * @description
 * @author: 王进
 **/
@MappedJdbcTypes(JdbcType.ARRAY)
@MappedTypes(List.class)
public class ListTypeHandler extends BaseTypeHandler<List<Object>> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<Object> objects, JdbcType jdbcType)
            throws SQLException {
        preparedStatement.setString(i, JsonUtils.objectToJson(objects));
    }

    @Override
    public List<Object> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return getObjects(resultSet.getString(s));
    }

    private List<Object> getObjects(String string) {
        if (string == null) {
            return null;
        }
        return new ArrayList<Object>(JsonUtils.jsonToList(string, Object.class));
    }

    @Override
    public List<Object> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return getObjects(resultSet.getString(i));
    }

    @Override
    public List<Object> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return getObjects(callableStatement.getString(i));
    }
}