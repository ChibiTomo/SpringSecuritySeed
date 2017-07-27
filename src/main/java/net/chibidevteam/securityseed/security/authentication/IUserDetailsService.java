package net.chibidevteam.securityseed.security.authentication;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserDetailsService extends UserDetailsService {

    @Override
    AuthUserDetails loadUserByUsername(String username);
}
