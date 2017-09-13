package net.chibidevteam.securityseed.service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import net.chibidevteam.securityseed.config.SecurityConfig;
import net.chibidevteam.securityseed.dto.UserAuthentication;
import net.chibidevteam.securityseed.security.authentication.ExpirableUserDetails;
import net.chibidevteam.securityseed.util.TokenHandler;

@Service
public class TokenAuthenticationService {

    private static final Log LOGGER = LogFactory.getLog(TokenAuthenticationService.class);

    @Autowired
    private SecurityConfig   config;

    private TokenHandler     tokenHandler;

    @PostConstruct
    public void init() {
        tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(config.getSecret()), config.getTokenAlgo());
    }

    public String createToken(Authentication authentication) {
        final ExpirableUserDetails user = (ExpirableUserDetails) authentication.getDetails();
        user.setExpires(System.currentTimeMillis() + config.getTokenExpiration());
        return tokenHandler.createTokenForUser(user);
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String authType = config.getAuthType();
        boolean useTokenParam = SecurityConfig.AUTH_TYPE_TOKEN_PARAM.equals(authType);
        boolean useTokenHeader = SecurityConfig.AUTH_TYPE_TOKEN_HEADER.equals(authType);

        String token = null;
        if (useTokenHeader) {
            token = request.getHeader(config.getTokenHeader());
        } else if (useTokenParam) {
            token = request.getParameter(config.getTokenParam());
        }
        return getAuthenticationFromToken(token);
    }

    private Authentication getAuthenticationFromToken(String token) {
        if (token != null) {
            // We will possibly lose some informations. It returns an ExpirableUserInfo
            // Think about adding some factory from ExpirableUserInfo
            UserDetails user = tokenHandler.parseUserFromToken(token);
            if (user != null) {
                return new UserAuthentication(user);
            }
        }
        return null;
    }

    public void addHeaderToken(HttpServletResponse response) {
        UserAuthentication auth = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String token = createToken(auth);
        response.addHeader(config.getTokenHeader(), token);
    }

}
