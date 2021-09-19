package com.github.wajncn.ext.mybatis.core;

import lombok.SneakyThrows;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 转换器接口。 T 为实体对象<entity>
 * 主要用于将前端参数转换为实体对象
 *
 * @author wajncn
 */
@SuppressWarnings("unchecked")
public interface InputConverter<T, Entity> extends BaseConverter {

    /**
     * 获取Entity实例
     *
     * @return Entity
     */
    @SneakyThrows
    default Entity entityClass() {
        Type[] genericInterfaces = this.getClass().getGenericInterfaces();
        if (genericInterfaces.getClass().isAssignableFrom(ParameterizedType[].class)) {
            for (Type genericInterface : genericInterfaces) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type type = actualTypeArguments[1];
                if (type instanceof Class) {
                    Class<Entity> clazz = (Class<Entity>) type;
                    return clazz.newInstance();
                }
            }
        }
        throw new IllegalArgumentException("由于参数化类型<Entity>为null,因此无法获取实际类型");
    }

    /**
     * 把当前对象转换成T 忽略NULL值
     *
     * @return new T
     */
    default Entity convertTo() {
        Entity entityClass = entityClass();
        copyProperties(this, entityClass);
        return entityClass;
    }

    /**
     * 把entity转换成T
     *
     */
    default T convertFrom(Entity entity) {
        if (entity == null) {
            return null;
        }
        copyProperties(entity, this);
        return (T) this;
    }

    /**
     * 更新对象 忽略NULL值
     *
     * @param entity 为被更改的对象
     */
    default void update(Entity entity) {
        copyProperties(this, entity);
    }

    /**
     * 把entity转换成T
     *
     */
    default List<T> convertFrom(List<Entity> entity) {
        if (CollectionUtils.isEmpty(entity)) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        entity.forEach(a -> {
            try {
                InputConverter t = this.getClass().newInstance();
                copyProperties(a, t);
                list.add((T) t);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

}

