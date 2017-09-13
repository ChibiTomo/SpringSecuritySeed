package net.chibidevteam.securityseed.dto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 2749872585692514247L;

    public UserAuthentication(UserDetails user) {
        super(user.getUsername(), user.getPassword(), user.getAuthorities());
        setDetails(user);
    }
}
