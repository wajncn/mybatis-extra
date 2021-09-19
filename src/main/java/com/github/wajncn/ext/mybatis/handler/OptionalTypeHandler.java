package com.github.wajncn.ext.mybatis.handler;

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
 * <p>
 * <p>
 * import javax.persistence.Column;
 * import javax.persistence.GeneratedValue;
 * import javax.persistence.Id;
 * import javax.persistence.Table;
 * import java.util.Optional;
 *
 * @author wajncn
 * @Table(name = "userinfo")
 * public class Userinfo {
 * @Id
 * @Column(name = "id")
 * @GeneratedValue(generator = "JDBC")
 * private Integer id;
 * @Column(name = "`name`")
 * private String name;
 * <p>
 * public Userinfo() {
 * }
 * <p>
 * public Userinfo(String name) {
 * this.name = name;
 * }
 * <p>
 * public Optional<Integer> getId() {
 * return Optional.ofNullable(id);
 * }
 * <p>
 * public Userinfo setId(Integer id) {
 * this.id = id;
 * return this;
 * }
 * <p>
 * public Optional<String> getName() {
 * return Optional.ofNullable(name);
 * }
 * <p>
 * public Userinfo setName(String name) {
 * this.name = name;
 * return this;
 * }
 * }
 * </p>
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