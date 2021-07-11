package com.wmeimob.fastboot.plus.aspect;

import com.wmeimob.fastboot.plus.annotations.FormLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author: 王进
 **/
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class FormLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;


    private static final ThreadLocal<String> STRING_THREAD_LOCAL = new ThreadLocal<>();

    @Around("@annotation(com.wmeimob.fastboot.plus.annotations.FormLimit)")
    public Object aroundMethod(ProceedingJoinPoint pjd) throws Throwable {
        Object result;
        //前置通知
        this.beforeProcess(pjd);
        try {
            result = pjd.proceed();
            //返回通知
        } catch (Throwable e) {
            //异常通知
            this.clear(pjd);
            throw e;
        } finally {
            STRING_THREAD_LOCAL.remove();
        }
        return result;
    }


    private void clear(final ProceedingJoinPoint pjd) {
        try {
            Optional.ofNullable(STRING_THREAD_LOCAL.get()).ifPresent(stringRedisTemplate::delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beforeProcess(final ProceedingJoinPoint pjd) {
        Object[] args = pjd.getArgs();
        StringBuilder sb = new StringBuilder("-1");
        for (Object arg : args) {
            Optional.ofNullable(arg).ifPresent(a -> {
                sb.append(a.hashCode());
            });
        }

        int user = Optional.ofNullable(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication).map(Authentication::getPrincipal).map(Object::hashCode).orElse(-1);
        long time = Optional.ofNullable(((MethodSignature) pjd.getSignature()).getMethod().getAnnotation(FormLimit.class)).map(FormLimit::time).orElse(5 * 1000L);
        String key = "FormLimit:".concat(pjd.getSignature().toString()).concat("-").concat(sb.toString()) + user;
        STRING_THREAD_LOCAL.set(key);
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, "SUCCESS", time, TimeUnit.MILLISECONDS);
        if (success == null || !success) {
            throw new IllegalArgumentException("请勿重复提交表单");
        }
    }
}