package tk.mybatis.plus.core;

import java.io.Serializable;

/**
 * 自定义枚举接口 若果你想返回数字 直接实现的 getValue 加 @JsonValue
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
