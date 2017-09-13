package net.chibidevteam.securityseed.security.httpbasic;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

public class HttpBasicEntryPoint extends BasicAuthenticationEntryPoint {

    private String realm;

    public HttpBasicEntryPoint(String realm) {
        this.realm = realm;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        String msg = authException.getMessage();
        if (msg == null || StringUtils.isEmpty(msg.trim())) {
            msg = "UNAUTHORIZED";
        }
        writer.println("HTTP Status 401 - " + msg.trim());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName(realm);
        super.afterPropertiesSet();
    }
}
