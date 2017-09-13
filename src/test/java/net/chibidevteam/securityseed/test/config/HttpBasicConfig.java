package net.chibidevteam.securityseed.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@PropertySource(AbstractConfig.PROPERTY_FILE_PATH + HttpBasicConfig.ID + AbstractConfig.PROPERTY_FILE_EXT)
@EnableWebMvc
public class HttpBasicConfig extends AbstractConfig {

    protected static final String ID = "httpBasic";
}
