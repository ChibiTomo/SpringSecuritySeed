package net.chibidevteam.securityseed.service;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import net.chibidevteam.securityseed.security.authentication.AuthUserDetails;
import net.chibidevteam.securityseed.security.authentication.IUserDetailsService;

@Component
public class CustomUserDetailService implements IUserDetailsService {

    @Override
    public AuthUserDetails loadUserByUsername(String username) {
        return new AuthUserDetails(username, "", new ArrayList<>());
    }

}
