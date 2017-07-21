package net.chibidevteam.restappliseed.main.dto;

import java.io.Serializable;

import net.chibidevteam.restappliseed.main.annotation.ParamName;

public class AuthenticationRequest implements Serializable {

    private static final long serialVersionUID = -1954410798919498236L;

    @ParamName("${net.chibidevteam.securityseed.login.param.username}")
    private String            username;
    @ParamName("${net.chibidevteam.securityseed.login.param.password}")
    private String            password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
