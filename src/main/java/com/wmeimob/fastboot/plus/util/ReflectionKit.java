package com.wmeimob.fastboot.plus.util;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;

/**
 * 反射工具类，提供反射相关的快捷操作
 *
 * @author: wajncn
 */
public final class ReflectionKit {

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存
     *
     * @param beanClass           类
     * @param withSuperClassFieds 是否包括父类的字段列表
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFieds) throws SecurityException {
        org.springframework.util.Assert.notNull(beanClass, "beanClass null");
        Field[] allFields = null;
        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                allFields = ArrayUtil.append(allFields, declaredFields);
            }
            searchType = withSuperClassFieds ? searchType.getSuperclass() : null;
        }

        return allFields;
    }

    /**
     * <p>
     * 反射对象获取泛型
     * </p>
     *
     * @param clazz      对象
     * @param genericIfc 所属泛型父类
     * @param index      泛型所在位置
     * @return Class
     */
    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final Class<?> genericIfc, final int index) {
        Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(ClassUtils.getUserClass(clazz), genericIfc);
        return null == typeArguments ? null : typeArguments[index];
    }
}
