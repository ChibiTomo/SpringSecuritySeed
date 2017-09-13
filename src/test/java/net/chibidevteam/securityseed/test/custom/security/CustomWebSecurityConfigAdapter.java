package net.chibidevteam.securityseed.test.custom.security;

import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_DISABLED_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_DISABLED_PASSWORD;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_DISABLED_ROLE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_PASSWORD;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_ROLE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_DISABLED_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_DISABLED_PASSWORD;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_DISABLED_ROLE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_PASSWORD;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_ROLE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;

import net.chibidevteam.securityseed.config.WebSecurityConfigAdapter;

@Configuration
@Order(99)
public class CustomWebSecurityConfigAdapter extends WebSecurityConfigAdapter {

    private static final Log LOGGER = LogFactory.getLog(CustomWebSecurityConfigAdapter.class);

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMem = auth.inMemoryAuthentication();

        inMem.withUser(USER_LOGIN).password(USER_PASSWORD).roles(USER_ROLE);
        inMem.withUser(ADMIN_LOGIN).password(ADMIN_PASSWORD).roles(ADMIN_ROLE);

        inMem.withUser(USER_DISABLED_LOGIN).password(USER_DISABLED_PASSWORD).roles(USER_DISABLED_ROLE).disabled(true);
        inMem.withUser(ADMIN_DISABLED_LOGIN).password(ADMIN_DISABLED_PASSWORD).roles(ADMIN_DISABLED_ROLE)
                .disabled(true);
    }
}
