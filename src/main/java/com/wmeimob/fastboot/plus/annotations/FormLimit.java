package com.wmeimob.fastboot.plus.annotations;

import java.lang.annotation.*;

/**
 * 防止表单提交
 *
 * @author: 王进
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormLimit {

    /**
     * 过期时间为毫秒 默认5000毫秒
     *
     * @return
     */
    long time() default 1000L * 5L;
}