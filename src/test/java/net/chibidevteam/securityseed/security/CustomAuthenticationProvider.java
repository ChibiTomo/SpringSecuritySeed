package net.chibidevteam.securityseed.security;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Log LOGGER = LogFactory.getLog(CustomAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication auth) {
        Object credential = auth.getCredentials();
        if (!(credential instanceof String)) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof String)) {
            return null;
        }
        String login = (String) principal;
        String pwd = (String) credential;
        LOGGER.debug("login: " + login + ", pwd: " + pwd);
        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(pwd)) {
            throw new InsufficientAuthenticationException("Missing login or password");
        }
        if (!"hello".equals(pwd)) {
            throw new BadCredentialsException("Wrong login and/or password");
        }

        return new UsernamePasswordAuthenticationToken(login, credential, new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
