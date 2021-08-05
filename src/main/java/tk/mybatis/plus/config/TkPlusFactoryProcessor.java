package tk.mybatis.plus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;


/**
 * 注册处理通用枚举和List Handler
 *
 * @author wajncn
 */
@Slf4j
public class TkPlusFactoryProcessor implements BeanFactoryPostProcessor {

    private static final String HANDLER_PACKAGE = "tk.mybatis.plus.handler";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            //配置文件中的属性
            final String typeHandlersPackage = Optional.ofNullable(beanFactory.getBean(Environment.class)
                            .getProperty("mybatis.typeHandlersPackage"))
                    .orElse(beanFactory.getBean(Environment.class).getProperty("mybatis.type-handlers-package"));
            Object myBatisProperties = null;
            try {
                myBatisProperties = beanFactory.getBean("myBatisProperties");
            } catch (BeansException ignored) {

            }
            if (myBatisProperties == null) {
                myBatisProperties = beanFactory.getBean("mybatis-tk.mybatis.mapper.autoconfigure.MybatisProperties");
            }
            final String consumerTypeHandlersPackage = typeHandlersPackage == null ? HANDLER_PACKAGE
                    : typeHandlersPackage.concat(",").concat(HANDLER_PACKAGE);
            myBatisProperties.getClass().getMethod("setTypeHandlersPackage", String.class)
                    .invoke(myBatisProperties, consumerTypeHandlersPackage);
            TkPlusConfigurer.registerHandler = true;
            log.warn("Register Handler [ListTypeHandler,MybatisEnumTypeHandler] Complete.");
        } catch (BeansException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
        }
    }


}
