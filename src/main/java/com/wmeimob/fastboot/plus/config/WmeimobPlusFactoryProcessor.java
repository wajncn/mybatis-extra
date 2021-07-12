package com.wmeimob.fastboot.plus.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;


/**
 * tk mapper处理通用枚举
 *
 * @author wajncn
 */
public class WmeimobPlusFactoryProcessor implements BeanFactoryPostProcessor {

    private static final String HANDLER_PACKAGE = "com.wmeimob.fastboot.plus.handler";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            //配置文件中的属性
            final String typeHandlersPackage = Optional.ofNullable(beanFactory.getBean(Environment.class).getProperty("mybatis.typeHandlersPackage"))
                    .orElse(beanFactory.getBean(Environment.class).getProperty("mybatis.type-handlers-package"));
            Object myBatisProperties = null;
            try {
                myBatisProperties = beanFactory.getBean("myBatisProperties");
            } catch (BeansException e) {

            }
            if (myBatisProperties == null) {
                myBatisProperties = beanFactory.getBean("mybatis-tk.mybatis.mapper.autoconfigure.MybatisProperties");
            }
            final String consumerTypeHandlersPackage = typeHandlersPackage == null ? HANDLER_PACKAGE : typeHandlersPackage.concat(",").concat(HANDLER_PACKAGE);
            myBatisProperties.getClass().getMethod("setTypeHandlersPackage", String.class).invoke(myBatisProperties, consumerTypeHandlersPackage);
            WmeimobPlusConfigurer.typeHandlerComplete = true;
        } catch (BeansException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
        }
    }


}
