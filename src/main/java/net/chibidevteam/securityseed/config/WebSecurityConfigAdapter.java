package net.chibidevteam.securityseed.config;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import net.chibidevteam.securityseed.security.httpbasic.HttpBasicEntryPoint;
import net.chibidevteam.securityseed.security.stateless.StatelessAuthenticationFilter;
import net.chibidevteam.securityseed.security.stateless.StatelessSuccessAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigAdapter extends WebSecurityConfigurerAdapter implements ApplicationContextAware {

    private static final Log       LOGGER = LogFactory.getLog(WebSecurityConfigAdapter.class);

    @Autowired
    private SecurityConfig         config;

    @Autowired(required = false)
    private AuthenticationProvider authProvider;
    @Autowired(required = false)
    private UserDetailsService     userDetailsService;

    private boolean                useTokenHeader;
    // @Autowired(required = false)
    // private AuthenticationDetailsSource<HttpServletRequest, ?> authDetailSource;

    private boolean                useTokenParam;

    private boolean                useHttpBasic;

    private boolean                useAuthByForm;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (authProvider != null) {
            auth.authenticationProvider(authProvider);
        }
        if (userDetailsService != null) {
            auth.userDetailsService(userDetailsService);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        String authType = config.getAuthType();
        useAuthByForm = SecurityConfig.AUTH_TYPE_FORM.equals(authType);
        useHttpBasic = SecurityConfig.AUTH_TYPE_HTTP_BASIC.equals(authType);
        useTokenParam = SecurityConfig.AUTH_TYPE_TOKEN_PARAM.equals(authType);
        useTokenHeader = SecurityConfig.AUTH_TYPE_TOKEN_HEADER.equals(authType);
        if (useTokenParam || useTokenHeader) {
            configureToken(http);
            LOGGER.trace("No logout for stateless service");
        } else if (useAuthByForm) {
            configureForm(http.formLogin());
            configureLogout(http.logout());
        } else if (useHttpBasic) {
            configureHttpBasic(http.httpBasic());
            LOGGER.trace("No logout for HTTP Basic Auth");
        } else {
            LOGGER.warn("Security configured but no correct authentication method found: '" + authType + "'");
        }

        configureUrlAuthorizations(http.authorizeRequests());

        LOGGER.info("Disabling CSRF: " + !config.useCSRF());
        if (!config.useCSRF()) {
            http.csrf().disable();
        }
        LOGGER.info("Disabling Anonymous: " + !config.isAllowAnonymous());
        if (!config.isAllowAnonymous()) {
            http.anonymous().disable();
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public StatelessAuthenticationFilter statelessAuthenticationFilter() {
        if (!useTokenHeader && !useTokenParam) {
            return null;
        }
        return new StatelessAuthenticationFilter();
    }

    @Bean
    public StatelessSuccessAuthenticationFilter statelessSuccessAuthenticationFilter() throws Exception {
        StatelessSuccessAuthenticationFilter authenticationTokenFilter = null;
        if (useTokenHeader || useTokenParam) {
            authenticationTokenFilter = new StatelessSuccessAuthenticationFilter();
            authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
            authenticationTokenFilter.setPasswordParameter(config.getPasswordParam());
            authenticationTokenFilter.setUsernameParameter(config.getUsernameParam());
            authenticationTokenFilter.setFilterProcessesUrl(config.getLoginProcess());
        }
        return authenticationTokenFilter;
    }

    @Bean
    public HttpBasicEntryPoint httpBasicEntryPoint() {
        return new HttpBasicEntryPoint(config.getBasicRealm());
    }

    private void configureToken(HttpSecurity http) throws Exception {
        LOGGER.info("Base configuration for stateless service");
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(statelessAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(statelessSuccessAuthenticationFilter(), StatelessAuthenticationFilter.class);
    }

    private void configureForm(FormLoginConfigurer<HttpSecurity> formLogin) {
        LOGGER.info("Configuring form authentication");
        // formLogin.authenticationDetailsSource(authDetailSource);

        formLogin.usernameParameter(config.getUsernameParam()) // default is username
                .passwordParameter(config.getPasswordParam()) // default is password
                .loginProcessingUrl(config.getLoginProcess()) // default is /login with an HTTP post
                .loginPage(config.getLoginPage()).permitAll() // default is /login with an HTTP get
                .failureUrl(config.getLoginError()).permitAll() // default is /login?error
                .defaultSuccessUrl(config.getDefaultLoginSuccess());
    }

    private void configureHttpBasic(HttpBasicConfigurer<HttpSecurity> httpBasic) throws Exception {
        LOGGER.info("Configuring HTTP Basic authentication");
        // httpBasic.authenticationDetailsSource(authDetailSource);
        httpBasic.authenticationEntryPoint(httpBasicEntryPoint());
    }

    private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
        LOGGER.trace("Configuring logout");

        logout.logoutUrl(config.getLogoutUrl());
        if (!StringUtils.isEmpty(config.getLogoutSuccessUrl())) {
            logout.logoutSuccessUrl(config.getLogoutSuccessUrl());
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
        if (!ArrayUtils.isEmpty(config.getDenyAll())) {
            LOGGER.info("DenyAll: " + StringUtils.join(config.getDenyAll(), ", "));
            getAuthorizedUrl(urlAuth, config.getDenyAll()).denyAll();
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
