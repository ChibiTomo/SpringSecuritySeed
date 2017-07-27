package net.chibidevteam.securityseed.testconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@PropertySource(AbstractConfig.PROPERTY_FILE_PATH + TokenParamConfig.ID + AbstractConfig.PROPERTY_FILE_EXT)
@EnableWebMvc
public class TokenParamConfig extends AbstractConfig {

    protected static final String ID = "tokenParam";
}
