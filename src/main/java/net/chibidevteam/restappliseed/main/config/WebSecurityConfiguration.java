package net.chibidevteam.restappliseed.main.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import net.chibidevteam.restappliseed.main.exception.NoAuthentificationProviderException;
import net.chibidevteam.restappliseed.main.security.rest.RESTAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter implements ApplicationContextAware {

    private static final Log LOGGER = LogFactory.getLog(WebSecurityConfiguration.class);

    @Autowired
    private SecurityConfig   config;

    // @Autowired
    // private RESTEntryPoint restEntryPoint;
    // @Autowired
    // private RESTAuthenticationFilter restAuthFilter;
    // @Autowired
    // private RESTAuthenticationSuccessHandler restAuthSuccessHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(getAuthProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        configureUrlAuthorizations(http.authorizeRequests());

        String authType = config.getAuthType();
        boolean useAuthByForm = SecurityConfig.AUTH_TYPE_FORM.equals(authType);
        boolean useHttpBasic = SecurityConfig.AUTH_TYPE_HTTP_BASIC.equals(authType);
        if (config.isREST()) {
            configureREST(http);
            if (useAuthByForm) {
                configureRESTForm(http.authorizeRequests());
            } else if (useHttpBasic) {
                configureRESTHttpBasic(http.httpBasic());
            } else {
                LOGGER.warn("REST configured but no correct authentication method found: '" + authType + "'");
            }
            LOGGER.trace("No logout for REST service");
        } else {
            if (useAuthByForm) {
                configureForm(http.formLogin());
                configureLogout(http.logout());
            } else if (useHttpBasic) {
                configureHttpBasic(http.httpBasic());
                LOGGER.trace("No logout for HTTP Basic Auth");
            } else {
                LOGGER.warn("Security configured but no correct authentication method found: '" + authType + "'");
            }
        }

        LOGGER.info("Disabling CSRF: " + !config.useCSRF());
        if (!config.useCSRF()) {
            http.csrf().disable();
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public RESTAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        RESTAuthenticationFilter authenticationTokenFilter = new RESTAuthenticationFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationTokenFilter;
    }

    private void configureREST(HttpSecurity http) throws Exception {
        LOGGER.info("Base configuration for REST service");
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //
                .and().addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    private void configureRESTForm(
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlAuth) {
        LOGGER.info("Configuring form authentication for REST service");
        LOGGER.info("PermitAll on: " + config.getLoginProcess() + "/**");
        urlAuth.antMatchers(config.getLoginProcess() + "/**").permitAll();
        // setAuthDetailSource(formLogin);
        //
        // commonConfigureForm(formLogin);
        // formLogin.successHandler(restAuthSuccessHandler) // Suppress redirection on success
        // .failureHandler(new SimpleUrlAuthenticationFailureHandler()) // Suppress redirection on failure
        // .and().exceptionHandling().authenticationEntryPoint(restEntryPoint);
    }

    private void configureRESTHttpBasic(HttpBasicConfigurer<HttpSecurity> httpBasic) {
        LOGGER.info("Configuring HTTP Basic authentication for REST service");
        setAuthDetailSource(httpBasic);
    }

    private void configureForm(FormLoginConfigurer<HttpSecurity> formLogin) {
        LOGGER.info("Configuring form authentication");
        setAuthDetailSource(formLogin);

        commonConfigureForm(formLogin);
        formLogin.loginPage(config.getLoginPage()).permitAll() // default is /login with an HTTP get
                .failureUrl(config.getLoginError()).permitAll() // default is /login?error
                .defaultSuccessUrl(config.getDefaultLoginSuccess());
    }

    private void configureHttpBasic(HttpBasicConfigurer<HttpSecurity> httpBasic) {
        LOGGER.info("Configuring HTTP Basic authentication");
        setAuthDetailSource(httpBasic);
    }

    private void commonConfigureForm(FormLoginConfigurer<HttpSecurity> formLogin) {
        formLogin.usernameParameter(config.getUsernameParam()) // default is username
                .passwordParameter(config.getPasswordParam()) // default is password
                .loginProcessingUrl(config.getLoginProcess());
    }

    private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
        LOGGER.trace("Configuring logout");

        logout.logoutUrl(config.getLogoutUrl());
        if (!StringUtils.isEmpty(config.getLogoutSuccessUrl())) {
            logout.logoutSuccessUrl(config.getLogoutSuccessUrl());
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
        if (!StringUtils.isEmpty(config.getAuthDetailSource())) {
            form.authenticationDetailsSource(getAuthDetailSource());
        }
    }

    private void setAuthDetailSource(HttpBasicConfigurer<HttpSecurity> basic) {
        if (!StringUtils.isEmpty(config.getAuthDetailSource())) {
            basic.authenticationDetailsSource(getAuthDetailSource());
        }
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
