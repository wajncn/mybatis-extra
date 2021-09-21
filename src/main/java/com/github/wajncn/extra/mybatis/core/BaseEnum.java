package com.github.wajncn.extra.mybatis.core;

import com.github.wajncn.extra.mybatis.handler.MybatisEnumTypeHandler;

import java.io.Serializable;

/**
 * 自定义枚举接口 若果你想返回数字 直接实现的 getValue 加 @JsonValue
 * 建议使用Integer或者String来使用枚举. 不要使用浮点型或者其他类型
 * 参见{@link MybatisEnumTypeHandler}
 *
 * @author wajncn
 **/
public interface BaseEnum<T extends Serializable> {

    /**
     * 枚举数据库存储值
     *
     * @return T
     */
    T getValue();
}
