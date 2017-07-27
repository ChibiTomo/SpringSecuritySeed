package net.chibidevteam.securityseed.dto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import net.chibidevteam.securityseed.security.authentication.AuthUserDetails;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 2749872585692514247L;

    public UserAuthentication(AuthUserDetails user) {
        super(user.getUsername(), user.getPassword(), user.getAuthorities());
        setDetails(user);
    }

}
