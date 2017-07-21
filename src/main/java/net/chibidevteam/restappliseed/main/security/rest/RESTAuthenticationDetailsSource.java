package net.chibidevteam.restappliseed.main.security.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import net.chibidevteam.restappliseed.main.config.SecurityConfig;
import net.chibidevteam.restappliseed.main.dto.AuthentificationToken;

@Component(SecurityConfig.REST_AUTH_DETAIL_SOURCE_NAME)
public class RESTAuthenticationDetailsSource
        implements AuthenticationDetailsSource<HttpServletRequest, AuthentificationToken> {

    @Override
    public AuthentificationToken buildDetails(HttpServletRequest context) {
        return new AuthentificationToken("UNIQUE_TOKEN");
    }

}
