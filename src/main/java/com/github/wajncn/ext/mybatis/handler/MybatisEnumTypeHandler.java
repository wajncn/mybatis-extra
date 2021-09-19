package com.github.wajncn.ext.mybatis.handler;

import com.github.wajncn.ext.mybatis.core.BaseEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

/**
 * 通用枚举
 *
 * @param <E>
 * @author wajncn
 */
@MappedTypes(BaseEnum.class)
public class MybatisEnumTypeHandler<E extends BaseEnum<?>> extends BaseTypeHandler<BaseEnum<?>> {

    /**
     * 枚举构造方法
     */
    private final E[] enumConstants;

    public MybatisEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        if (!BaseEnum.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.format("NoSuchMethod getValue() in Class: %s.", type.getName()));
        }
        try {
            enumConstants = type.getEnumConstants();
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
        Object value = rs.getObject(columnName);
        if (null == value && rs.wasNull()) {
            return null;
        }
        return this.valueOf(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        if (null == value && rs.wasNull()) {
            return null;
        }
        return this.valueOf(value);
    }


    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        if (null == value && cs.wasNull()) {
            return null;
        }
        return this.valueOf(value);
    }


    private E valueOf(Object value) {
        return Arrays.stream(this.enumConstants)
                .filter((baseEnum) -> this.equalsValue(value, baseEnum.getValue()))
                .findFirst()
                .orElse(null);
    }


    /**
     * 值比较
     *
     * @param sourceValue 数据库字段值
     * @param targetValue 当前枚举属性值
     * @return 是否匹配
     */
    protected boolean equalsValue(Object sourceValue, Object targetValue) {
        String sValue = String.valueOf(sourceValue).trim();
        String tValue = String.valueOf(targetValue).trim();
        if (sourceValue instanceof Number && targetValue instanceof Number
                && new BigDecimal(sValue).compareTo(new BigDecimal(tValue)) == 0) {
            return true;
        }
        return Objects.equals(sValue, tValue);
    }
}
