package net.chibidevteam.restappliseed;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AuthentificationToken extends AbstractAuthenticationToken {

    public AuthentificationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        // TODO Auto-generated constructor stub
    }

    private static final long serialVersionUID = 5713349463606576719L;

    @Override
    public Object getCredentials() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }

}
