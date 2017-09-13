package net.chibidevteam.securityseed.test.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import net.chibidevteam.securityseed.test.stateless.CustomUserDetailService;

public class AbstractStatelessConfig extends AbstractConfig implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanDefinition userDetailsService = new RootBeanDefinition(CustomUserDetailService.class,
                RootBeanDefinition.AUTOWIRE_BY_TYPE, true);
        registry.registerBeanDefinition("userDetailsService", userDetailsService);
    }
}
