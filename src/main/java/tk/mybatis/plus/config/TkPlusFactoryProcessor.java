package tk.mybatis.plus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.util.Optional;


/**
 * 注册处理Handler
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
            Object myBatisProperties = this.getBean(beanFactory, "myBatisProperties");
            if (myBatisProperties == null) {
                myBatisProperties = this.getBean(beanFactory, "mybatis-tk.mybatis.mapper.autoconfigure.MybatisProperties");
            }
            if (myBatisProperties == null) {
                myBatisProperties = this.getBean(beanFactory, "mybatis-org.mybatis.spring.boot.autoconfigure.MybatisProperties");
            }
            if (myBatisProperties == null) {
                //通过第二种方法注册Handler
                log.warn("Start using TkPlusConfigurer Register Handler");
                return;
            }
            final String consumerTypeHandlersPackage = typeHandlersPackage == null ? HANDLER_PACKAGE
                    : typeHandlersPackage.concat(",").concat(HANDLER_PACKAGE);
            // 这种方式注册Handler比较好. 可以做到不需要处理xml的type_handler啦
            myBatisProperties.getClass().getMethod("setTypeHandlersPackage", String.class)
                    .invoke(myBatisProperties, consumerTypeHandlersPackage);
            TkPlusConfigurer.registerHandler = true;
            log.warn("Register Handler [ListTypeHandler,MybatisEnumTypeHandler] Complete.");
        } catch (Exception ignored) {
            // 默认方式注册失败. 通过第二种方法注册Handler
            log.warn("Register Handler error, Start using TkPlusConfigurer Register Handler");
        }
    }


    private Object getBean(@NonNull ConfigurableListableBeanFactory beanFactory, @NonNull String beanName) {
        try {
            return beanFactory.getBean(beanName);
        } catch (Exception ignored) {
            return null;
        }
    }

}
