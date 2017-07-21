package net.chibidevteam.restappliseed.main.config;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringValueResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.chibidevteam.restappliseed.main.annotation.RenamingProcessor;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter
        implements EmbeddedValueResolverAware, ApplicationContextAware {

    private static final Log    LOGGER = LogFactory.getLog(WebMvcConfiguration.class);
    private ApplicationContext  applicationContext;
    private StringValueResolver placeholderResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        LOGGER.info("Adding renaming processor");

        argumentResolvers.add(renamingProcessor());
        super.addArgumentResolvers(argumentResolvers);
    }

    public RenamingProcessor renamingProcessor() {
        RenamingProcessor rp = new RenamingProcessor(true);
        rp.setApplicationContext(applicationContext);
        rp.setEmbeddedValueResolver(placeholderResolver);
        return rp;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }
}
