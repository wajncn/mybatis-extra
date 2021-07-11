package com.wmeimob.fastboot.plus.config;

import com.wmeimob.fastboot.plus.converter.BaseEnumConverterFactory;
import com.wmeimob.fastboot.plus.enums.BaseEnum;
import com.wmeimob.fastboot.plus.handler.ListTypeHandler;
import com.wmeimob.fastboot.plus.handler.MybatisEnumTypeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 处理通用枚举
 *
 * @author wajncn
 * @author snowxuyu
 */
@Slf4j
@RequiredArgsConstructor
public class WmeimobPlusConfig implements InitializingBean, WebMvcConfigurer {

    private final SqlSessionFactory sqlSessionFactory;

    /**
     * 是否已经处理过枚举
     */
    static volatile boolean typeHandlerComplete = false;

    private static final String TYPE_HANDLER_MAP = "TYPE_HANDLER_MAP";


    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        if (typeHandlerComplete) {
            log.info("wmeimob plus==> handler register[ListTypeHandler,MybatisEnumTypeHandler] complete.");
            return;
        }
        try {
            final TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
            final Field jdbcTypeHandlerMap = TypeHandlerRegistry.class.getDeclaredField(TYPE_HANDLER_MAP);
            jdbcTypeHandlerMap.setAccessible(true);
            final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = (Map<Type, Map<JdbcType, TypeHandler<?>>>) jdbcTypeHandlerMap.get(typeHandlerRegistry);
            typeHandlerMap.keySet().stream().map(Class.class::cast).filter(clzType -> clzType != null && clzType.isEnum() && BaseEnum.class.isAssignableFrom(clzType))
                    .forEach(clzType -> typeHandlerRegistry.register(clzType, MybatisEnumTypeHandler.class));
            typeHandlerRegistry.register(List.class, ListTypeHandler.class);
            log.info("wmeimob plus==> handler register[ListTypeHandler,MybatisEnumTypeHandler] complete.");
        } catch (Exception ignored) {
            log.error("wmeimob plus==> handler register fail.");
        }
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new BaseEnumConverterFactory());
        log.info("wmeimob plus==> registry addConverterFactory BaseEnumConverterFactory complete.");
    }
}
