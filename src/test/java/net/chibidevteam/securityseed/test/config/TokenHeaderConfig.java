package net.chibidevteam.securityseed.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(AbstractConfig.PROPERTY_FILE_PATH + TokenHeaderConfig.ID + AbstractConfig.PROPERTY_FILE_EXT)
// @EnableWebMvc
public class TokenHeaderConfig extends AbstractStatelessConfig {

    protected static final String ID = "tokenHeader";
}
