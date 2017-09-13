package net.chibidevteam.securityseed.security.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import net.chibidevteam.utils.helper.ClassHelper;

public class ExpirableUserDetails extends User {

    private static final long serialVersionUID = 854990947645505936L;

    private long              expires          = 0;

    public ExpirableUserDetails() {
        this("Empty", "", new ArrayList<>());
    }

    public ExpirableUserDetails(UserDetails userDetails) {
        this(userDetails.getUsername(), userDetails.getPassword(), userDetails.isEnabled(),
                userDetails.isAccountNonExpired(), userDetails.isCredentialsNonExpired(),
                userDetails.isAccountNonLocked(), userDetails.getAuthorities());
    }

    public ExpirableUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public ExpirableUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired() && new Date().getTime() < expires;
    }

    @Override
    public boolean equals(Object obj) {
        return ClassHelper.areEquals(ExpirableUserDetails.class, this, obj);
    }

    @Override
    public int hashCode() {
        return ClassHelper.hash(ExpirableUserDetails.class, this);
    }

}
