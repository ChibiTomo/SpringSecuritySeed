package net.chibidevteam.securityseed.test.config;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan({ AbstractConfig.CONFIG_PACKAGE, AbstractConfig.SERVICE_PACKAGE, AbstractConfig.CUSTOM_PACKAGE,
        AbstractConfig.CONTROLLER_PACKAGE })
public class AbstractConfig {

    protected static final String PROPERTY_FILE_PATH            = "classpath:/securityseed-";
    protected static final String PROPERTY_FILE_EXT             = ".properties";

    protected static final String CONFIG_PACKAGE                = "net.chibidevteam.securityseed.config";
    protected static final String SECURITY_PACKAGE              = "net.chibidevteam.securityseed.security";
    protected static final String SERVICE_PACKAGE               = "net.chibidevteam.securityseed.service";
    protected static final String CUSTOM_PACKAGE                = "net.chibidevteam.securityseed.test.custom";
    protected static final String CONTROLLER_PACKAGE            = "net.chibidevteam.securityseed.test.controller";

    public static final String    PUBLIC_BASE_PATH              = "/public";
    public static final String    SECURED_BASE_PATH             = "/admin";
    public static final String    SUB_PATH                      = "/hello";

    public static final String    AUTH_HEADER                   = "Authorization";
    public static final String    AUTH_HEADER_RESPONSE          = "WWW-Authenticate";

    public static final String    PUBLIC_RESPONSE               = "Hello in public zone.";
    public static final String    PUBLIC_AUTHENTICATED_RESPONSE = "Authenticated in public";
    public static final String    SECURED_RESPONSE              = "You are authenticated";

    public static final String    WRONG_LOGIN                   = "dfjvbdfbjhnmfv";
    public static final String    WRONG_PASSWORD                = "sav;jVNVOI";

    public static final String    USER_LOGIN                    = "user";
    public static final String    USER_PASSWORD                 = "userPwd";
    public static final String    USER_ROLE                     = "USER";

    public static final String    USER_DISABLED_LOGIN           = "userDisabled";
    public static final String    USER_DISABLED_PASSWORD        = "userDisabledPwd";
    public static final String    USER_DISABLED_ROLE            = "USER";

    public static final String    ADMIN_LOGIN                   = "admin";
    public static final String    ADMIN_PASSWORD                = "superAdminPwd";
    public static final String    ADMIN_ROLE                    = "ADMIN";

    public static final String    ADMIN_DISABLED_LOGIN          = "adminDisabled";
    public static final String    ADMIN_DISABLED_PASSWORD       = "superAdminDisabledPwd";
    public static final String    ADMIN_DISABLED_ROLE           = "ADMIN";

}
