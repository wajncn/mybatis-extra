package com.wmeimob.fastboot.plus.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;


/**
 * 针对wmeimob fastboot项目处理通用枚举
 *
 * @author wajncn
 */
public class WmeimobPlusFactoryProcessor implements BeanFactoryPostProcessor {

    private static final String HANDLER_PACKAGE = "com.wmeimob.fastboot.plus.handler";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            String property = beanFactory.getBean(Environment.class).getProperty("fastboot.logic-delete-key");
            if (StringUtils.hasText(property)) {
                WmeimobPluSetting.setLogicDeleteKey(property);
            }
        } catch (BeansException ignored) {
        }

        try {
            //配置文件中的属性
            final String typeHandlersPackage = beanFactory.getBean(Environment.class).getProperty("mybatis.typeHandlersPackage");
            final Object myBatisProperties = beanFactory.getBean("myBatisProperties");
            final String consumerTypeHandlersPackage = typeHandlersPackage == null ? HANDLER_PACKAGE : typeHandlersPackage.concat(",").concat(HANDLER_PACKAGE);
            myBatisProperties.getClass().getMethod("setTypeHandlersPackage", String.class).invoke(myBatisProperties, consumerTypeHandlersPackage);
            WmeimobPlusConfig.typeHandlerComplete = true;
        } catch (BeansException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
        }
    }
}
