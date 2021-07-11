package com.wmeimob.fastboot.plus.handler;

import com.wmeimob.fastboot.plus.enums.BaseEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
@Slf4j
@MappedTypes(value = {BaseEnum.class})
public class MybatisEnumTypeHandler<E extends Enum<?>> extends BaseTypeHandler<Enum<?>> {

    private final Class<E> type;

    private final Method method;

    public MybatisEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        if (BaseEnum.class.isAssignableFrom(type)) {
            try {
                this.method = type.getMethod("getValue");
                this.method.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(String.format("NoSuchMethod getValue() in Class: %s.", type.getName()));
            }
        } else {
            log.warn("this.method is null");
            method = null;
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Enum<?> parameter, JdbcType jdbcType)
            throws SQLException {
        try {
//            this.method.setAccessible(true);
            if (jdbcType == null) {
                ps.setObject(i, this.method.invoke(parameter));
            } else {
                ps.setObject(i, this.method.invoke(parameter), jdbcType.TYPE_CODE);
            }
        } catch (IllegalAccessException e) {
            log.error("unrecognized jdbcType, failed to set StringValue for type=" + parameter);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(String.format("Error: NoSuchMethod in %s.  Cause:", this.type.getName()));
        }
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
        return Arrays.stream(this.type.getEnumConstants()).filter((e) -> equalsValue(value, getValue(e))).findAny().orElse(null);
    }


    private Object getValue(Object object) {
        try {
            return this.method.invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(String.format(
                    "Error: NoSuchMethod in %s.  Cause:",
                    type.getName()
            ));
        }
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
