package net.chibidevteam.securityseed.security.stateless;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import net.chibidevteam.securityseed.service.TokenAuthenticationService;

public class StatelessAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication auth = tokenAuthenticationService.getAuthentication((HttpServletRequest) request);
        SecurityContextHolder.getContext().setAuthentication(auth);
        logger.debug("Populated SecurityContextHolder with '" + auth + "'");

        chain.doFilter(request, response);
    }
}
