package net.chibidevteam.restappliseed.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource({ "classpath:/default-securityseed.properties", "classpath:/securityseed.properties" })
public class SecurityConfig {

    public static final String ANY_REQUEST_PATTERN          = "/**";
    public static final String AUTH_TYPE_FORM               = "form";
    public static final String AUTH_TYPE_HTTP_BASIC         = "http_basic";

    public static final String REST_AUTH_DETAIL_SOURCE_NAME = "RESTAuthDetailSource";

    @Value("${net.chibidevteam.securityseed.authProviderName}")
    private String             authProviderName;

    @Value("${net.chibidevteam.securityseed.permitAll}")
    private String[]           permitAll;
    @Value("${net.chibidevteam.securityseed.anonymous}")
    private String[]           anonymous;
    @Value("${net.chibidevteam.securityseed.denyAll}")
    private String[]           denyAll;
    @Value("${net.chibidevteam.securityseed.rememberMe}")
    private String[]           rememberMe;
    @Value("${net.chibidevteam.securityseed.authenticated}")
    private String[]           authenticated;
    @Value("${net.chibidevteam.securityseed.fullyAuthenticated}")
    private String[]           fullyAuthenticated;

    @Value("${net.chibidevteam.securityseed.login.page}")
    private String             loginPage;
    @Value("${net.chibidevteam.securityseed.login.error}")
    private String             loginError;
    @Value("${net.chibidevteam.securityseed.login.process}")
    private String             loginProcess;
    @Value("${net.chibidevteam.securityseed.login.defaultSuccess}")
    private String             defaultLoginSuccess;
    @Value("${net.chibidevteam.securityseed.login.param.username}")
    private String             usernameParam;
    @Value("${net.chibidevteam.securityseed.login.param.password}")
    private String             passwordParam;

    @Value("${net.chibidevteam.securityseed.login.token.header}")
    private String             tokenHeader;
    @Value("${net.chibidevteam.securityseed.login.token.param}")
    private String             tokenParam;

    @Value("${net.chibidevteam.securityseed.logout.url}")
    private String             logoutUrl;
    @Value("${net.chibidevteam.securityseed.logout.success}")
    private String             logoutSuccessUrl;

    @Value("${net.chibidevteam.securityseed.useCSRF}")
    private boolean            useCSRF;
    @Value("${net.chibidevteam.securityseed.isREST}")
    private boolean            isREST;

    @Value("${net.chibidevteam.securityseed.authType}")
    private String             authType;

    @Value("${net.chibidevteam.securityseed.authDetailSource}")
    private String             authDetailSource;

    public String getAuthProviderName() {
        return authProviderName;
    }

    public String[] getPermitAll() {
        return permitAll;
    }

    public String[] getAnonymous() {
        return anonymous;
    }

    public String[] getDenyAll() {
        return denyAll;
    }

    public String[] getRememberMe() {
        return rememberMe;
    }

    public String[] getAuthenticated() {
        return authenticated;
    }

    public String[] getFullyAuthenticated() {
        return fullyAuthenticated;
    }

    public String getLoginPage() {
        return loginPage;
    }

    public String getLoginError() {
        return loginError;
    }

    public String getLoginProcess() {
        return loginProcess;
    }

    public String getDefaultLoginSuccess() {
        return defaultLoginSuccess;
    }

    public String getUsernameParam() {
        return usernameParam;
    }

    public String getPasswordParam() {
        return passwordParam;
    }

    public boolean useCSRF() {
        return useCSRF;
    }

    public String getAuthDetailSource() {
        return authDetailSource;
    }

    public boolean isREST() {
        return isREST;
    }

    public String getAuthType() {
        return authType;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public String getLogoutSuccessUrl() {
        return logoutSuccessUrl;
    }

    public String getTokenHeader() {
        return tokenHeader;
    }

    public String getTokenParam() {
        return tokenParam;
    }
}
