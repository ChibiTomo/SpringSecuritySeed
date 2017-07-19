package net.chibidevteam.restappliseed.config;

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import net.chibidevteam.restappliseed.auth.rest.RESTAuthenticationSuccessHandler;
import net.chibidevteam.restappliseed.auth.rest.RESTEntryPoint;
import net.chibidevteam.restappliseed.auth.rest.RESTLogoutSuccessHandler;
import net.chibidevteam.restappliseed.exception.NoAuthentificationProviderException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter implements ApplicationContextAware {

    private static final Log                 LOGGER = LogFactory.getLog(WebSecurityConfiguration.class);

    @Autowired
    private SecurityConfig                   config;

    @Autowired
    private RESTEntryPoint                   restEntryPoint;
    @Autowired
    private RESTAuthenticationSuccessHandler restAuthSuccessHandler;
    @Autowired
    private RESTLogoutSuccessHandler         restLogoutSuccessHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(getAuthProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlAuth = http
                .authorizeRequests();

        configureUrlAuthorizations(urlAuth);
        if (!StringUtils.isEmpty(config.getAuthDetailSource())) {
            urlAuth.and().httpBasic();
        }

        String authType = config.getAuthType();
        if (SecurityConfig.AUTH_TYPE_FORM.equals(authType)) {
            LOGGER.info("Configuring form authentication");
            FormLoginConfigurer<HttpSecurity> form = urlAuth.and().formLogin();
            configureFormAuthentification(form);
            setAuthDetailSource(form);
        } else if (SecurityConfig.AUTH_TYPE_HTTP_BASIC.equals(authType)) {
            LOGGER.info("Configuring HTTP Basic authentication");
            HttpBasicConfigurer<HttpSecurity> basic = urlAuth.and().httpBasic();
            setAuthDetailSource(basic);
        } else {
            LOGGER.warn("No authentication method configured");
        }

        configureLogout(urlAuth.and().logout());

        if (!config.useCSRF()) {
            http.csrf().disable();
        }
    }

    private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
        logout.logoutUrl(config.getLogoutUrl());
        logout.logoutUrl(config.getLogoutUrl());
        if (config.isREST()) {
            logout.logoutSuccessHandler(restLogoutSuccessHandler);
        } else {
            logout.logoutSuccessUrl(config.getLogoutSuccessUrl());
        }
    }

    private void configureFormAuthentification(FormLoginConfigurer<HttpSecurity> form) throws Exception {
        form.usernameParameter(config.getUsernameParam()) // default is username
                .passwordParameter(config.getPasswordParam()) // default is password
                .loginProcessingUrl(config.getLoginProcess()) // default is /login
                .defaultSuccessUrl(config.getDefaultLoginSuccess());

        if (config.isREST()) {
            LOGGER.info("Configuring for a REST service");

            form.successHandler(restAuthSuccessHandler) // Suppress redirection on success
                    .failureHandler(new SimpleUrlAuthenticationFailureHandler()) // Suppress redirection on success
                    .and().exceptionHandling().authenticationEntryPoint(restEntryPoint); //
        } else {
            form.loginPage(config.getLoginPage()).permitAll() // default is /login with an HTTP get
                    .failureUrl(config.getLoginError()).permitAll() // default is /login?error
                    .defaultSuccessUrl(config.getDefaultLoginSuccess());
        }

    }

    private AuthenticationProvider getAuthProvider() throws NoAuthentificationProviderException {
        ApplicationContext ctx = getApplicationContext();
        try {
            Object bean = ctx.getBean(config.getAuthProviderName());
            if (!(bean instanceof AuthenticationProvider)) {
                throw new NoAuthentificationProviderException("Bean with name '" + config.getAuthProviderName()
                        + "' does not implement AuthenticationProvider");
            }
            return (AuthenticationProvider) bean;
        } catch (NoSuchBeanDefinitionException e) {
            throw new NoAuthentificationProviderException(
                    "Cannot find bean with name '" + config.getAuthProviderName() + "'", e);
        }
    }

    private void setAuthDetailSource(FormLoginConfigurer<HttpSecurity> form) {
        form.authenticationDetailsSource(getAuthDetailSource());
    }

    private void setAuthDetailSource(HttpBasicConfigurer<HttpSecurity> basic) {
        basic.authenticationDetailsSource(getAuthDetailSource());
    }

    @SuppressWarnings("unchecked")
    private AuthenticationDetailsSource<HttpServletRequest, ?> getAuthDetailSource() {
        ClassLoader cl = getClass().getClassLoader();
        String authDetailSource = config.getAuthDetailSource();
        try {
            Class<?> clazz = cl.loadClass(authDetailSource);
            Constructor<?> ctor = clazz.getConstructor(ApplicationContext.class);
            return (AuthenticationDetailsSource<HttpServletRequest, ?>) ctor.newInstance(getApplicationContext());
        } catch (Exception e) {
            LOGGER.warn("'" + authDetailSource + "' is an invalid AuthenticationDetailsSource<HttpServletRequest, ?>. "
                    + "None will be used", e);
            return null;
        }
    }

    private void configureUrlAuthorizations(
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlAuth) {
        if (!ArrayUtils.isEmpty(config.getPermitAll())) {
            LOGGER.info("PermitAll: " + StringUtils.join(config.getPermitAll(), ", "));
            getAuthorizedUrl(urlAuth, config.getPermitAll()).permitAll();
        }
        if (!ArrayUtils.isEmpty(config.getAnonymous())) {
            LOGGER.info("Anonymous: " + StringUtils.join(config.getAnonymous(), ", "));
            getAuthorizedUrl(urlAuth, config.getAnonymous()).anonymous();
        }
        if (!ArrayUtils.isEmpty(config.getDenyAll())) {
            LOGGER.info("DenyAll: " + StringUtils.join(config.getDenyAll(), ", "));
            getAuthorizedUrl(urlAuth, config.getDenyAll()).denyAll();
        }
        if (!ArrayUtils.isEmpty(config.getRememberMe())) {
            LOGGER.info("RememberMe: " + StringUtils.join(config.getRememberMe(), ", "));
            getAuthorizedUrl(urlAuth, config.getRememberMe()).rememberMe();
        }
        if (!ArrayUtils.isEmpty(config.getAuthenticated())) {
            LOGGER.info("Authenticated: " + StringUtils.join(config.getAuthenticated(), ", "));
            getAuthorizedUrl(urlAuth, config.getAuthenticated()).authenticated();
        }
        if (!ArrayUtils.isEmpty(config.getFullyAuthenticated())) {
            LOGGER.info("FullyAuthenticated: " + StringUtils.join(config.getFullyAuthenticated(), ", "));
            getAuthorizedUrl(urlAuth, config.getFullyAuthenticated()).fullyAuthenticated();
        }

    }

    private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl getAuthorizedUrl(
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlAuth,
            String[] patterns) {
        if (ArrayUtils.contains(patterns, SecurityConfig.ANY_REQUEST_PATTERN)) {
            return urlAuth.anyRequest();
        }
        return urlAuth.antMatchers(patterns);
    }
}
