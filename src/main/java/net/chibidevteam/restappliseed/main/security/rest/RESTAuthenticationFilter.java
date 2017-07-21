package net.chibidevteam.restappliseed.main.security.rest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import net.chibidevteam.restappliseed.main.config.SecurityConfig;
import net.chibidevteam.restappliseed.main.service.TokenManagerService;

public class RESTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private UserDetailsService  userDetailsService;
    @Autowired
    private TokenManagerService tokenManagerService;
    @Autowired
    private SecurityConfig      config;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = getToken(httpRequest);
        String username = tokenManagerService.getUsernameFromToken(authToken);

        if (username != null && auth == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (tokenManagerService.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(config.getTokenHeader());
        if (StringUtils.isEmpty(token)) {
            token = httpRequest.getParameter(config.getTokenParam());
        }
        return token;
    }
}
