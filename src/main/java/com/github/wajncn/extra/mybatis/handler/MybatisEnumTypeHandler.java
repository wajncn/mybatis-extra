package com.github.wajncn.extra.mybatis.handler;

import com.github.wajncn.extra.mybatis.core.BaseEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.lang.Nullable;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 通用枚举
 *
 * @param <E>
 * @author wajncn
 */
@MappedTypes(BaseEnum.class)
public class MybatisEnumTypeHandler<E extends BaseEnum<?>> extends BaseTypeHandler<BaseEnum<?>> {

    /**
     * 将枚举的构造方法缓存起来. 在转换的时候直接取即可.
     */
    private final Map<String, E> enumMap = new HashMap<>();

    public MybatisEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        if (!BaseEnum.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.format("NoSuchMethod getValue() in Class: %s.", type.getName()));
        }
        try {
            E[] enumConstants = type.getEnumConstants();
            if (enumConstants == null) {
                return;
            }
            for (E enumConstant : enumConstants) {
                enumMap.put(enumConstant.getValue() + "", enumConstant);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("NoSuchMethod getValue() in Class: %s.", type.getName()));
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BaseEnum<?> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setObject(i, parameter.getValue());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.valueOf(rs.getObject(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.valueOf(rs.getObject(columnIndex));
    }


    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.valueOf(cs.getObject(columnIndex));
    }


    private E valueOf(@Nullable Object value) {
        return Optional.ofNullable(value)
                .map(Object::toString)
                .map(enumMap::get)
                .orElse(null);
    }
}
