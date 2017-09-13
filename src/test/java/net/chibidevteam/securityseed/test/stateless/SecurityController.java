package net.chibidevteam.securityseed.test.stateless;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chibidevteam.securityseed.dto.UserAuthentication;
import net.chibidevteam.securityseed.service.TokenAuthenticationService;
import net.chibidevteam.securityseed.test.custom.model.AuthenticationResponse;

@RestController
@RequestMapping("${net.chibidevteam.securityseed.login.process}")
public class SecurityController {

    @Autowired
    private TokenAuthenticationService tokenManagerService;

    public SecurityController(TokenAuthenticationService tokenManagerService) {
        this.tokenManagerService = tokenManagerService;
    }

    @RequestMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<AuthenticationResponse> successJSON(HttpServletResponse response,
            HttpServletRequest request) {
        UserAuthentication auth = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String token = tokenManagerService.createToken(auth);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
