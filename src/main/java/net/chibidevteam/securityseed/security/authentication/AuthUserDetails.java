package net.chibidevteam.securityseed.security.authentication;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import net.chibidevteam.utils.helper.ClassHelper;

public class AuthUserDetails extends User {

    private static final long serialVersionUID = 854990947645505936L;

    private long              expires;

    public AuthUserDetails() {
        this("Empty", "", new ArrayList<>());
    }

    public AuthUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public AuthUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    @Override
    public boolean equals(Object obj) {
        return ClassHelper.areEquals(AuthUserDetails.class, this, obj);
    }

    @Override
    public int hashCode() {
        return ClassHelper.hash(AuthUserDetails.class, this);
    }

}
