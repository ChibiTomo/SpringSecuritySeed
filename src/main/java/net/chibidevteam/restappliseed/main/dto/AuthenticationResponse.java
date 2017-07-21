package net.chibidevteam.restappliseed.main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import net.chibidevteam.restappliseed.main.annotation.ParamName;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class AuthenticationResponse {

    @ParamName("${net.chibidevteam.securityseed.login.param.token}")
    private String token;

    public AuthenticationResponse(String token) {
        this.setToken(token);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
