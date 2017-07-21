package net.chibidevteam.restappliseed.main.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;

import net.chibidevteam.restappliseed.secret.controller.RESTAuthenticationController;

@Configuration
public class ControllerConfig implements BeanDefinitionRegistryPostProcessor {

    private static final Log LOGGER = LogFactory.getLog(ControllerConfig.class);

    @Autowired
    private SecurityConfig   config;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // Not implemented
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        // if (config.isREST()) {
        LOGGER.info("Loading authentication controller for REST service");
        BeanDefinition beanDefinition = new RootBeanDefinition(RESTAuthenticationController.class,
                Autowire.BY_TYPE.value(), true);
        registry.registerBeanDefinition("RESTAuthenticationController", beanDefinition);
        // }
    }

}
