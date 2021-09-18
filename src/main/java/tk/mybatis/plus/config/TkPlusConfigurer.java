package tk.mybatis.plus.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tk.mybatis.plus.converter.BaseEnumConverterFactory;
import tk.mybatis.plus.core.BaseEnum;
import tk.mybatis.plus.handler.ListTypeHandler;
import tk.mybatis.plus.handler.MybatisEnumTypeHandler;
import tk.mybatis.plus.handler.OptionalTypeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 处理通用枚举
 *
 * @author wajncn
 * @author snowxuyu
 */
@Slf4j
@RequiredArgsConstructor
public class TkPlusConfigurer implements InitializingBean, WebMvcConfigurer {

    private final SqlSessionFactory sqlSessionFactory;
    /**
     * 是否已经处理过handler
     */
    static volatile boolean registerHandler = false;

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        if (registerHandler) {
            return;
        }
        try {
            //双重注册。 防止TkPlusFactoryProcessor注册失败
            final TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration()
                    .getTypeHandlerRegistry();
            final Field jdbcTypeHandlerMap = TypeHandlerRegistry.class.getDeclaredField("TYPE_HANDLER_MAP");
            jdbcTypeHandlerMap.setAccessible(true);
            final Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = (Map<Type, Map<JdbcType, TypeHandler<?>>>) jdbcTypeHandlerMap.get(typeHandlerRegistry);
            typeHandlerMap.keySet().stream().map(Class.class::cast)
                    .filter(clzType -> clzType != null && clzType.isEnum() && BaseEnum.class.isAssignableFrom(clzType))
                    .forEach(clzType -> typeHandlerRegistry.register(clzType, MybatisEnumTypeHandler.class));
            typeHandlerRegistry.register(List.class, ListTypeHandler.class);
            typeHandlerRegistry.register(Optional.class, OptionalTypeHandler.class);
            //这种方式注册TypeHandler成功后 如果使用了xml. 只需要配置xml的ListTypeHandler. 其他handler暂时不用配置
            log.info("Register Handler [ListTypeHandler(Need Configuration XML),MybatisEnumTypeHandler,OptionalTypeHandler] Complete.");
        } catch (Exception ignored) {
            log.error("Register Handler Fail.");
        }
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new BaseEnumConverterFactory());
        log.info("Add ConverterFactory BaseEnumConverterFactory complete.");
    }
}
