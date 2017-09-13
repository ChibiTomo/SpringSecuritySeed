package net.chibidevteam.securityseed.test.custom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class AuthenticationResponse {

    private String theTokenParam;

    public AuthenticationResponse() {
        // Default ctor
    }

    public AuthenticationResponse(String token) {
        this.setTheTokenParam(token);
    }

    public String getTheTokenParam() {
        return theTokenParam;
    }

    public void setTheTokenParam(String theTokenParam) {
        this.theTokenParam = theTokenParam;
    }

}
