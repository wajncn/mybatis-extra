package com.wmeimob.fastboot.plus.core;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.NonNull;

import java.beans.FeatureDescriptor;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * DTO的转换器接口。
 *
 * @author wajncn
 */
@SuppressWarnings("unchecked")
interface BaseConverter extends Serializable {

    /**
     * @param source
     * @param target
     */
    default void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, BaseConverterUtils.getNullPropertyNames(source));
    }


    class BaseConverterUtils {
        /**
         * 获取属性的空名称集。
         *
         * @param source 对象数据不能为空
         * @return 属性的空名称集
         */
        public static String[] getNullPropertyNames(@NonNull Object source) {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(source);

            return Stream.of(beanWrapper.getPropertyDescriptors()).filter(propertyDescriptor -> {
                String propertyName = propertyDescriptor.getName();
                Object propertyValue = beanWrapper.getPropertyValue(propertyName);
                return propertyValue == null;
            }).map(FeatureDescriptor::getName).distinct().toArray(String[]::new);
        }
    }


}


