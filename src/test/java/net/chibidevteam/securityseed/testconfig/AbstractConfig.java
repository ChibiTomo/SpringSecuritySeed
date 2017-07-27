package net.chibidevteam.securityseed.testconfig;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan({ AbstractConfig.CONFIG_PACKAGE, AbstractConfig.SERVICE_PACKAGE, AbstractConfig.SECURITY_PACKAGE,
        AbstractConfig.CONTROLLER_PACKAGE })
public class AbstractConfig {

    protected static final String PROPERTY_FILE_PATH            = "classpath:/securityseed-";
    protected static final String PROPERTY_FILE_EXT             = ".properties";
    protected static final String CONFIG_PACKAGE                = "net.chibidevteam.securityseed.config";
    protected static final String CONTROLLER_PACKAGE            = "net.chibidevteam.securityseed.controller";
    protected static final String SERVICE_PACKAGE               = "net.chibidevteam.securityseed.service";
    protected static final String SECURITY_PACKAGE              = "net.chibidevteam.securityseed.security";

    public static final String    PUBLIC_BASE_PATH              = "/public";
    public static final String    SECURED_BASE_PATH             = "/admin";
    public static final String    SUB_PATH                      = "/hello";

    public static final String    PUBLIC_RESPONSE               = "Hello in public zone.";
    public static final String    PUBLIC_AUTHENTICATED_RESPONSE = "Authenticated in public";
    public static final String    SECURED_RESPONSE              = "You are authenticated";

}
