package net.chibidevteam.securityseed.security.stateless;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import net.chibidevteam.securityseed.config.SecurityConfig;
import net.chibidevteam.securityseed.dto.UserAuthentication;
import net.chibidevteam.securityseed.service.TokenAuthenticationService;

public class StatelessSuccessAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private UserDetailsService         userDetailsService;
    @Autowired
    private TokenAuthenticationService tokenManagerService;
    @Autowired
    private SecurityConfig             config;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException, ServletException {
        final UserDetails details = userDetailsService.loadUserByUsername(authentication.getName());
        final UserAuthentication userAuthentication = new UserAuthentication(details);

        // Add the authentication to the Security context
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
        logger.debug("Populated SecurityContextHolder with '" + userAuthentication + "'");

        if (SecurityConfig.AUTH_TYPE_TOKEN_HEADER.equals(config.getAuthType())) {
            tokenManagerService.addHeaderToken(response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
