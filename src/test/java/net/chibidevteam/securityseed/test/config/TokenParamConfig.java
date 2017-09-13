package net.chibidevteam.securityseed.test.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import net.chibidevteam.securityseed.test.stateless.SecurityController;

@Configuration
@PropertySource(AbstractConfig.PROPERTY_FILE_PATH + TokenParamConfig.ID + AbstractConfig.PROPERTY_FILE_EXT)
@EnableWebMvc
public class TokenParamConfig extends AbstractStatelessConfig {

    protected static final String ID = "tokenParam";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        super.postProcessBeanDefinitionRegistry(registry);

        BeanDefinition securityController = new RootBeanDefinition(SecurityController.class,
                RootBeanDefinition.AUTOWIRE_BY_TYPE, true);
        registry.registerBeanDefinition("securityController", securityController);
    }
}
