package net.chibidevteam.securityseed.test.stateless;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import net.chibidevteam.securityseed.security.authentication.ExpirableUserDetails;

@Component
public class CustomUserDetailService implements UserDetailsService {

    @Override
    public ExpirableUserDetails loadUserByUsername(String username) {
        return new ExpirableUserDetails(username, "", new ArrayList<>());
    }

}
